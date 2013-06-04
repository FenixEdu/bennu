package pt.ist.bennu.bennu.scheduler.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.scheduler.log.ExecutionLog;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonStreamParser;

@RunWith(JUnit4.class)
public class TestExecutionLog {
    private static final Logger logger = LoggerFactory.getLogger(TestExecutionLog.class);

    private static Gson gson;
    private static final String JSON_PATH = "/tmp/executionLog.json";
    private static Map<String, ExecutionLog> logs;

    private static Object JSON_FILE_LOCK = new Object();

    @BeforeClass
    public static void setup() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DateTime.class, new JsonSerializer<DateTime>() {

            @Override
            public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.toString());
            }
        });
        gsonBuilder.registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {

            @Override
            public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                return new DateTime(json.getAsString());
            }
        });

        gson = gsonBuilder.create();
        logs = new ConcurrentHashMap();
    }

    public ExecutionLog addLog() {
        ExecutionLog test = new ExecutionLog("xpto");
        final DateTime now = new DateTime();
        test.setStart(now);
        test.setEnd(now.plusMinutes(5));
        logs.put(test.getId(), test);
        writeJson(gson.toJson(test));
        return test;
    }

    public static void dumpToFile() {
        final Collection<ExecutionLog> values = logs.values();
        if (values != null && !values.isEmpty()) {
            writeJson(gson.toJson(values));
        }
    }

    public static void readFromFile() {
        synchronized (JSON_FILE_LOCK) {
            logs.clear();
            try (FileReader fileReader = new FileReader(new File(JSON_PATH))) {
                JsonParser parser = new JsonParser();
                JsonElement logsArray = parser.parse(fileReader);
                if (logsArray != null && logsArray.isJsonArray()) {
                    for (JsonElement el : logsArray.getAsJsonArray()) {
                        ExecutionLog executionLog = new ExecutionLog((JsonObject) el);
                        logs.put(executionLog.getId(), executionLog);
                    }
                } else {
                    logger.info("no file to read");
                }
            } catch (IOException e) {
                throw new Error(e);
            }
        }
    }

    private class LogRunnable implements Runnable {

        private final Integer id;

        public LogRunnable(Integer id) {
            this.id = id;
        }

        @Override
        public void run() {
            System.out.println("Running " + id);
            for (int i = 0; i < 500; i++) {
                addLog();
                try {
                    System.out.printf("%d Waiting %d seconds \n", id, id + 1);
                    Thread.sleep((id + 1) * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static void writeJson(String json) {
        if (Strings.isNullOrEmpty(json)) {
            return;
        }
        synchronized (JSON_FILE_LOCK) {
            try (FileOutputStream fos = new FileOutputStream(new File(JSON_PATH), true)) {
                fos.write(json.getBytes());
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void dryrun() {
        System.out.println("Running tests ...");
    }

//    @Test
    public void doit() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.execute(new Runnable() {

            @Override
            public void run() {
                do {
                    System.out.println("read from file");
                    readSingleJsonFromFile();
                    try {
                        System.out.println("read from file: sleep 1 sec");
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (true);
            }
        });

        for (int i = 0; i < 10; i++) {
            executor.execute(new LogRunnable(i));
        }

        // This will make the executor accept no new threads
        // and finish all existing threads in the queue
        executor.shutdown();
        // Wait until all threads are finish
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Finished all threads");
    }

    //@Test
    public void writeFile() throws IOException {
        final long millis = new DateTime().getMillis();
        final File file = new File("/tmp/test");
        file.createNewFile();
        final FileOutputStream fos = new FileOutputStream(file);
        fos.write(Strings.repeat("A", 1024 * 1024 * 50).getBytes());
        fos.flush();
        fos.close();
        final long millis2 = new DateTime().getMillis();
        System.out.println(millis2 - millis);
    }

    //@Test
    public void readSingleJsonFromFile() {
        try (FileReader fileReader = new FileReader(new File(JSON_PATH))) {
            JsonStreamParser parser = new JsonStreamParser(fileReader);
            while (parser.hasNext()) {
                final JsonElement jsonElLog = parser.next();
                System.out.println("log " + jsonElLog.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
