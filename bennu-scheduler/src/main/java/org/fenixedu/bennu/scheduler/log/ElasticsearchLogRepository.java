package org.fenixedu.bennu.scheduler.log;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class ElasticsearchLogRepository implements ExecutionLogRepository {

    private static final String LOG = "task";
    private static final String LOG_OUTPUT = "task-output";
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
        final RestClient restClient;
        if (!Strings.isNullOrEmpty(username)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

            final RestClientBuilder builder = RestClient.builder(new HttpHost(connectUrl, port, scheme))
                    .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                            .setDefaultCredentialsProvider(credentialsProvider));
            restClient = builder.build();
        } else {
            restClient = RestClient.builder(new HttpHost(connectUrl, port)).build();
        }

        final ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        this.client = new ElasticsearchClient(transport);
    }

    private String dateString(final DateTime date) {
        return date.getYear() + "." + date.getMonthOfYear();
    }

    private String getIndexForTask(final DateTime date) {
        return this.indexPrefix + "-" + LOG + "-" + dateString(date);
    }

    private String getIndexForTaskOutput(final DateTime date) {
        return this.indexPrefix + "-" + LOG_OUTPUT + "-" + dateString(date);
    }

    private String getIndexesFor(final Function<DateTime, String> getIndexByDaTe) {
        DateTime date = now();
        String indexes = getIndexByDaTe.apply(date);
        for (int n = 1; n < keepIndexesNumberOfMonths; n++) {
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

    private void store(final ExecutionLog log, final Optional<String> previous) {
        try {
            final JsonObject json = log.json();
            previous.ifPresent(prev -> json.addProperty("previous", prev));
            write(json, getIndexForTask(now()), log.getId());
        } catch (Exception ex) {
            throw new RuntimeException("Error storing scheduler log of " + log.getId(), ex);
        }

    }

    private void write(final JsonObject json, final String index, final String id) throws IOException {
        final Reader input = new StringReader(json.toString());
        final IndexRequest<JsonData> request = IndexRequest.of(i -> i
                .index(index)
                .id(id)
                .withJson(input));
        this.client.index(request);
        this.client.indices().refresh();
    }

    private JsonObject readIndexJson() {
        try {
            // Do an aggregated search by taskName to get all task types
            createIndexesIfNecessary(getIndexesForTask());
            final List<String> indices = List.of(getIndexesForTask().split(","));
            final String aggName = "aggregateByTaskName";
            final SearchResponse<ObjectNode> response = client.search(
                    s -> s.index(indices).aggregations(aggName, a -> a.terms(terms -> terms.field("taskName").size(1000))),
                    ObjectNode.class);
            String[] taskNames = response.aggregations().get(aggName).sterms().buckets().array().stream()
                    .map(i -> i.key()._get().toString()).toArray(String[]::new);
            JsonObject json = new JsonObject();
            if (taskNames.length <= 0) {
                return json;
            }

            // Someone that knows java can do this for loop in parallel for more speed
            for (String name : taskNames) {
                final SearchResponse<ObjectNode> itemResponse = client.search(
                        s -> s
                            .index(indices)
                            .query(q -> q.term(t -> t.field("taskName").value(name)))
                            .sort(sort -> sort.field(f -> f.field("start").order(SortOrder.Desc)))
                            .size(1),
                        ObjectNode.class);
                json.addProperty(itemResponse.hits().hits().get(0).source().get("taskName").toString(),
                        itemResponse.hits().hits().get(0).id());
            }
            return json;
        } catch (final Exception ex) {
            throw new RuntimeException("Error reading scheduler indexer", ex);
        }
    }

    private Optional<String> read(final String index, final String id) {
        return read(index, id, false);
    }

    private Optional<String> read(final String index, final String id, final boolean stopRecovery) {
        try {
            final SearchResponse<ObjectNode> response = this.client.search(g -> g
                    .index(List.of(index.split(",")))
                    .query(q -> q.term(t -> t.field("_id").value(id))),
                    ObjectNode.class);
            if (response.hits().hits().size() > 0) {
                return Optional.of(response.hits().hits().get(0).source().toString());
            }
        } catch (final Exception ex) {
            if (ex.getMessage().contains("index_not_found_exception") && !stopRecovery) {
                createIndexesIfNecessary(index);
                return read(index, id, true);
            }
            throw new RuntimeException("Error reading scheduler log of " + id, ex);
        }
        return Optional.empty();
    }

    private Optional<JsonObject> readJson(final String index, final String id) throws IOException {
        Optional<String> content = read(index, id);
        if (content.isPresent()) {
            return Optional.of(JsonParser.parseString(content.get()).getAsJsonObject());
        }
        return Optional.empty();
    }

    private Optional<String> previousIdOf(final ExecutionLog log) {
        try {
            return readJson(getIndexesForTask(), log.getId()).map(obj -> obj.getAsJsonPrimitive("previous")).map(JsonPrimitive::getAsString);
        } catch (Exception ex) {
            throw new RuntimeException("Error calculating scheduler previus log of " + log.getId(), ex);
        }
    }

    @Override
    public void update(final ExecutionLog log) {
        store(log, previousIdOf(log));
    }

    @Override
    public void newExecution(final ExecutionLog log) {
        synchronized (this) {
            final JsonObject json = readIndexJson();
            final Optional<String> previous =
                    Optional.ofNullable(json.getAsJsonPrimitive(log.getTaskName())).map(JsonPrimitive::getAsString);
            store(log, previous);
        }
    }

    @Override
    public void appendTaskLog(final ExecutionLog log, final String text) {
        try {
            final UpdateRequest request = UpdateRequest.of(i -> i
                    .index(getIndexForTaskOutput(now()))
                    .type(LOG_OUTPUT)
                    .id(log.getId())
                    .upsert(JsonData.fromJson("{\"output\":\"\"}"))
                    .scriptedUpsert(true)
                    .script(builder -> builder
                            .inline(s -> s
                                    .lang("painless")
                                    .source("ctx._source.output +=params.output;")
                                    .params("output", JsonData.of(text)))));
            this.client.update(request, JsonData.class);
            // We don't refresh the index, because a task may have to many call to
            // appendTaskLog, better to wait for 1s (normal time for elastic to index)
        } catch (final ResponseException re) {
            if (re.getResponse().getStatusLine().getStatusCode() == HttpStatus.SC_CONFLICT) {
                // concurrent writting - try again
                appendTaskLog(log, text);
            } else {
                throw new RuntimeException("Error appending output to scheduler in log" + log.getId(), re);
            }
        } catch (final Exception ex) {
            throw new RuntimeException("Error appending output to scheduler in log" + log.getId(), ex);
        }
    }

    private void writeFile(final String path, final byte[] bytes, final boolean append) {
        final File file = new File(path);
        file.getParentFile().mkdirs();
        try (final FileOutputStream stream = new FileOutputStream(file, append)) {
            stream.write(bytes);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<byte[]> readFile(final String path) {
        final File file = new File(path);
        if (!file.exists()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.toByteArray(file));
        } catch (final IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private String getFilePathForLog(final ExecutionLog log, final String filename) {
        final DateTime dateOfStart = log.getStart();
        return this.basePathFiles + "/" + dateOfStart.getYear() + "/" + dateOfStart.getMonthOfYear() + "/"
                + dateOfStart.getDayOfMonth() + "/" + log.getId() + "_" + filename;
    }

    @Override
    public void storeFile(final ExecutionLog log, final String fileName, final byte[] contents, final boolean append) {
        // Store file in fileSystem with year/month/day/allFiles structure
        writeFile(getFilePathForLog(log, fileName), contents, append);
    }

    @Override
    public Stream<ExecutionLog> latest() {
        return readIndexJson().entrySet().stream()
                .map(entry -> getLog(entry.getKey(), entry.getValue().getAsString()).orElse(null))
                .filter(Objects::nonNull);
    }

    @Override
    public Stream<ExecutionLog> executionsFor(final String taskName, final Optional<String> startId, final int max) {
        return executionsFor(taskName, startId, max, false);
    }

    public Stream<ExecutionLog> executionsFor(final String taskName, final Optional<String> startId, final int max, final boolean stopRecovery) {
        try {
            final Set<ExecutionLog> result = new HashSet<>();
            final SearchResponse<ObjectNode> response = this.client.search(g -> g
                    .index(List.of(getIndexesForTask().split(",")))
                    .query(q -> q.term(t -> t.field("taskName").value(taskName)))
                    .sort(f -> f.field(t -> t.field("start").order(SortOrder.Desc))),
                    ObjectNode.class);
            final List<Hit<ObjectNode>> hits = response.hits().hits();
            for (final Hit<ObjectNode> hit : hits) {
                final ExecutionLog log = new ExecutionLog(JsonParser.parseString(hit.source().toString()).getAsJsonObject());
                result.add(log);
                if (result.size() >= max) {
                    break;
                }
            }
            return result.stream();
        } catch (final Exception ex) {
            if (ex.getMessage().contains("index_not_found_exception") && !stopRecovery) {
                createIndexesIfNecessary(getIndexesForTask());
                return executionsFor(taskName, startId, max, true);
            }
            throw new RuntimeException("Error search for tasks of type " + taskName, ex);
        }
    }

    @Override
    public Optional<String> getTaskLog(final String taskName, final String id) {
        try {
            return readJson(getIndexesForTaskOutput(), id).map(jsonObject -> jsonObject.get("output").getAsString());
        } catch (Exception ex) {
            throw new RuntimeException("Error getting log" + id, ex);
        }
    }

    @Override
    public Optional<byte[]> getFile(final String taskName, final String id, final String fileName) {
        final ExecutionLog log = getLog(taskName, id).get();
        return readFile(getFilePathForLog(log, fileName));
    }

    @Override
    public Optional<ExecutionLog> getLog(final String taskName, final String id) {
        try {
            return readJson(getIndexesForTask(), id).map(ExecutionLog::new);
        } catch (Exception ex) {
            throw new RuntimeException("Error getting log" + id, ex);
        }
    }

    // Hack: Workaround to ensure elasticsearch doesn't throw exception "index doesn't exist" during search
    private void createIndexesIfNecessary(final String index) {
        try {
            for (final String indexName : index.split(",")) {
                if (!this.client.indices().exists(e -> e.index(indexName)).value()) {
                    // The taskName field must be of type keyword to allow aggregations
                    final JsonObject type = new JsonObject();
                    type.addProperty("type", "keyword");
                    final JsonObject taskName = new JsonObject();
                    taskName.add("taskName", type);
                    final JsonObject properties = new JsonObject();
                    properties.add("properties", taskName);
                    final JsonObject mappings = new JsonObject();
                    mappings.add("mappings", properties);

                    final Reader input = new StringReader(mappings.toString());
                    this.client.indices().create(c -> c.index(indexName).withJson(input));
                }
            }
        } catch (final Exception ex) {
            throw new RuntimeException("Error checking indexes validity", ex);
        }
    }

}
