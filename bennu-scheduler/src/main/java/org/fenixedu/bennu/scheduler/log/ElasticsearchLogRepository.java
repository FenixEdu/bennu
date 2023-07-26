package org.fenixedu.bennu.scheduler.log;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.elasticsearch.client.RestClientBuilder;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.joda.time.DateTime;

public class ElasticsearchLogRepository implements ExecutionLogRepository {

    private static final String INDEX_INDEXER = "indexer";
    private static final String LOG = "task";
    private static final String LOG_OUTPUT = "task-output";
    private static final JsonParser parser = new JsonParser();
    private String basePathFiles;
    private String indexPrefix;
    private int keepIndexesNumberOfMonths;

    private ElasticsearchClient client;

    private DateTime now() {
        return DateTime.now();
    }

    public ElasticsearchLogRepository(String connectUrl, int port, String indexPrefix, String username, String password, String scheme, int searchMonths) {
        // The log path in the context of elasticsearch is only used for files generated in a task execution
        this.basePathFiles = SchedulerSystem.getLogsPath();
        this.indexPrefix = indexPrefix;
        this.keepIndexesNumberOfMonths = searchMonths;
        RestClient restClient;
        if(!Strings.isNullOrEmpty(username)){
            final CredentialsProvider credentialsProvider =
                    new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password));

            RestClientBuilder builder = RestClient.builder(
                            new HttpHost(connectUrl, port, scheme))
                    .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                        @Override
                        public HttpAsyncClientBuilder customizeHttpClient(
                                HttpAsyncClientBuilder httpClientBuilder) {
                            return httpClientBuilder
                                    .setDefaultCredentialsProvider(credentialsProvider);
                        }
                    });
            restClient = builder.build();
        } else {
            restClient = RestClient.builder(
                    new HttpHost(connectUrl, port)).build();
        }

        ElasticsearchTransport transport = new RestClientTransport(restClient , new JacksonJsonpMapper());
        this.client = new ElasticsearchClient(transport);
    }
    private String getIndexForIndexer(){
        return this.indexPrefix+"-"+INDEX_INDEXER;
    }

    private String dateString(DateTime date){
        return date.getYear()+"."+date.getMonthOfYear();
    }

    private String getIndexForTask(DateTime date) {
        return this.indexPrefix+"-"+LOG+"-"+dateString(date);
    }

    private String getIndexForTaskOutput(DateTime date) {
        return this.indexPrefix+"-"+LOG_OUTPUT+"-"+dateString(date);
    }

    private String getIndexesFor(Function<DateTime, String> getIndexByDaTe){
        DateTime date = now();
        String indexes = getIndexByDaTe.apply(date);
        for (int n = 1; n < keepIndexesNumberOfMonths;n++) {
            date = date.minusMonths(1);
            indexes += "," + getIndexByDaTe.apply(date);
        }
        return indexes;
    }
    private String getIndexesForTask() {
        return getIndexesFor(this::getIndexForTask);
    }

    private String getIndexesForTaskOutput() {
        return getIndexesFor(this::getIndexForTaskOutput);
    }

    private void store(ExecutionLog log, Optional<String> previous) {
        try {
            JsonObject json = log.json();
            previous.ifPresent(prev -> json.addProperty("previous", prev));
            write(json,getIndexForTask(now()), "task", log.getId());
        } catch (Exception ex) {
            throw new RuntimeException("Error storing scheduler log of " + log.getId(), ex);
        }

    }

    private void write(JsonObject json, String index, String type, String id) throws IOException {
        Reader input = new StringReader(json.toString());
        IndexRequest<JsonData> request = IndexRequest.of(i -> i
                .index(index)
                .type(type)
                .id(id)
                .withJson(input)
        );
        IndexResponse response = this.client.index(request);
        this.client.indices().refresh();
    }

    private JsonObject readIndexJson() {
        try {
            return readJson(getIndexForIndexer(), INDEX_INDEXER).orElseGet(JsonObject::new);
        } catch (Exception ex) {
            throw new RuntimeException("Error reading scheduler indexer", ex);
        }
    }

    private Optional<String> read(String index, String id) {
        return read(index, id, false);
    }

    private Optional<String> read(String index, String id, boolean stopRecovery) {
        try {
            SearchResponse<ObjectNode> response = this.client.search(g -> g
                            .index(List.of(index.split(",")))
                            .query(q -> q.term(t -> t.field("_id").value(id))),
                    ObjectNode.class
            );
            if(response.hits().hits().size() > 0) {
                return Optional.of(response.hits().hits().get(0).source().toString());
            }
        } catch (Exception ex) {
            if(ex.getMessage().contains("index_not_found_exception") && !stopRecovery){
                createIndexesIfNecessary(index);
                return read(index, id, true);
            }
            throw new RuntimeException("Error reading scheduler log of " + id, ex);
        }
        return Optional.empty();
    }

    private Optional<JsonObject> readJson(String index, String id) throws IOException {
        Optional<String> content = read(index, id);
        if(content.isPresent()) {
            return Optional.of(parser.parse(content.get()).getAsJsonObject());
        }
        return Optional.empty();
    }

    private Optional<String> previousIdOf(ExecutionLog log) {
        try{
            return readJson(getIndexesForTask(), log.getId()).map(obj -> obj.getAsJsonPrimitive("previous")).map(JsonPrimitive::getAsString);
        } catch (Exception ex) {
            throw new RuntimeException("Error calculating scheduler previus log of " + log.getId(), ex);
        }
    }

    @Override
    public void update(ExecutionLog log) {
            store(log, previousIdOf(log));
    }

    @Override
    public void newExecution(ExecutionLog log) {
        synchronized (this) {
            JsonObject json = readIndexJson();
            Optional<String> previous =
                    Optional.ofNullable(json.getAsJsonPrimitive(log.getTaskName())).map(JsonPrimitive::getAsString);
            json.addProperty(log.getTaskName(), log.getId());
            store(log, previous);
            try {
                write(json, getIndexForIndexer(), INDEX_INDEXER, INDEX_INDEXER);
            } catch (Exception ex) {
                throw new RuntimeException("Error writing to scheduler index", ex);
            }
        }
    }

    @Override
    public void appendTaskLog(ExecutionLog log, String text) {
        try{
            UpdateRequest request = UpdateRequest.of(i -> i
                    .index(getIndexForTaskOutput(now()))
                    .type(LOG_OUTPUT)
                    .id(log.getId())
                    .upsert(JsonData.fromJson("{\"output\":\"\"}"))
                    .scriptedUpsert(true)
                    .script(builder -> builder
                            .inline(s -> s
                                    .lang("painless")
                                    .source("ctx._source.output +=params.output;")
                                    .params("output", JsonData.of("\n"+ text)))));
            this.client.update(request, JsonData.class);
            // We don't refresh the index, because a task may have to many call to appendTaskLog, better to wait for 1s (normal time for elastic to index)
        } catch (Exception ex) {
            throw new RuntimeException("Error appending output to scheduler in log" + log.getId(), ex);
        }
    }

    private void writeFile(String path, byte[] bytes, boolean append) {
        File file = new File(path);
        file.getParentFile().mkdirs();
        try (FileOutputStream stream = new FileOutputStream(file, append)) {
            stream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<byte[]> readFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.toByteArray(file));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private String getFilePathForLog(ExecutionLog log, String filename) {
        DateTime dateOfStart = log.getStart();
        return this.basePathFiles + "/" + dateOfStart.getYear() + "/" + dateOfStart.getMonthOfYear() + "/" + dateOfStart.getDayOfMonth() + "/" + log.getId()+"_"+filename;
    }

    @Override
    public void storeFile(ExecutionLog log, String fileName, byte[] contents, boolean append) {
        // Store file in fileSystem with year/month/day/allFiles structure
        writeFile(getFilePathForLog(log, fileName), contents, append);
    }

    @Override
    public Stream<ExecutionLog> latest() {
        return readIndexJson().entrySet().stream()
                .map(entry -> getLog(entry.getKey(), entry.getValue().getAsString()).orElse(null)).filter(Objects::nonNull);
    }

    @Override
    public Stream<ExecutionLog> executionsFor(String taskName, Optional<String> startId, int max) {
        return executionsFor(taskName, startId, max, false);
    }

    public Stream<ExecutionLog> executionsFor(String taskName, Optional<String> startId, int max, boolean stopRecovery) {
        try {
            Set<ExecutionLog> result = new HashSet<>();
            SearchResponse<ObjectNode> response = this.client.search(g -> g
                            .index(List.of(getIndexesForTask().split(",")))
                            .query(q -> q.term(t -> t.field("taskName.keyword").value(taskName)))
                            .sort(f -> f.field(t -> t.field("start").order(SortOrder.Desc))),
                    ObjectNode.class
            );
            List<Hit<ObjectNode>> hits = response.hits().hits();
            for (Hit<ObjectNode> hit: hits) {
                ExecutionLog log = new ExecutionLog(parser.parse(hit.source().toString()).getAsJsonObject());
                result.add(log);
                if(result.size() >= max) {
                    break;
                }
            }
            return result.stream();
        } catch (Exception ex) {
            if(ex.getMessage().contains("index_not_found_exception") && !stopRecovery){
                createIndexesIfNecessary(getIndexesForTask());
                return executionsFor(taskName, startId, max, true);
            }
            throw new RuntimeException("Error search for tasks of type " + taskName, ex);
        }
    }

    @Override
    public Optional<String> getTaskLog(String taskName, String id) {
        try {
            return readJson(getIndexesForTaskOutput(), id).map(jsonObject -> jsonObject.get("output").getAsString());
        } catch (Exception ex) {
            throw new RuntimeException("Error getting log" + id, ex);
        }
    }

    @Override
    public Optional<byte[]> getFile(String taskName, String id, String fileName) {
        ExecutionLog log = getLog(taskName,id).get();
        return readFile(getFilePathForLog(log, fileName));
    }

    @Override
    public Optional<ExecutionLog> getLog(String taskName, String id) {
        try {
            return readJson(getIndexesForTask(), id).map(ExecutionLog::new);
        } catch (Exception ex) {
            throw new RuntimeException("Error getting log" + id, ex);
        }
    }

    // Hack: Workaround to ensure elasticsearch doesn't throw exception "index doesn't exist" during search
    private void createIndexesIfNecessary(String index) {
        try {
            for(String indexName :index.split(",")){
                if(!this.client.indices().exists(e -> e.index(indexName)).value()) {
                    this.client.indices().create(c -> c.index(indexName));
                }
            }
        }catch (Exception ex) {
            throw new RuntimeException("Error checking indexes validaty", ex);
        }
    }

}
