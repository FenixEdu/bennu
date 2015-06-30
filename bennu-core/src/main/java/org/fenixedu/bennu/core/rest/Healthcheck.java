package org.fenixedu.bennu.core.rest;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.google.gson.JsonObject;

/**
 * An {@link Healthcheck} provides information about the health of a system depended on by the application (typically an external
 * system).
 * 
 * This class was greatly inspired by <a href="http://metrics.codahale.com">Metrics</a>.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
public abstract class Healthcheck {

    /**
     * The result of a {@link Healthcheck}. It can be healthy or unhealthy (with either an error message or a thrown exception).
     */
    public static class Result {

        /**
         * Returns a healthy {@link Result}.
         * 
         * @return {@link Result} instance with "OK" message
         */
        public static Result healthy() {
            return new Result(true, "OK", null);
        }

        /**
         * Returns a healthy {@link Result} with the given message.
         * 
         * @param message the message to include in the result
         * @return {@link Result} instance
         */
        public static Result healthy(String message) {
            return new Result(true, message, null);
        }

        /**
         * Returns an unhealthy {@link Result} with the given message.
         * 
         * @param message the message to include in the result
         * @return {@link Result} instance
         */
        public static Result unhealthy(String message) {
            return new Result(false, message, null);
        }

        /**
         * Returns an unhealthy {@link Result} with the given error.
         * 
         * @param error an exception to include in the result
         * @return {@link Result} instance
         */
        public static Result unhealthy(Throwable error) {
            return new Result(false, error.getMessage(), error);
        }

        /**
         * Returns an unhealthy {@link Result} with the given error and message.
         * 
         * @param message the message to include in the result
         * @param error an exception to include in the result
         * 
         * @return {@link Result} instance
         */
        public static Result unhealthy(String message, Throwable error) {
            return new Result(false, message, error);
        }

        private final boolean healthy;
        private final String message;
        private final Throwable error;

        private Result(boolean isHealthy, String message, Throwable error) {
            this.healthy = isHealthy;
            this.message = message;
            this.error = error;
        }

        public JsonObject toJson() {
            JsonObject json = new JsonObject();

            json.addProperty("healthy", healthy);
            json.addProperty("message", message);
            if (error != null) {
                StringWriter writer = new StringWriter();
                error.printStackTrace(new PrintWriter(writer));
                json.addProperty("error", writer.toString());
            }

            return json;
        }

    }

    /**
     * Returns the presentation name of this healthcheck.
     * 
     * @return the name {@link String}
     */
    public abstract String getName();

    /**
     * Perform a check of the application component.
     *
     * @return if the component is healthy, a healthy {@link Result}; otherwise, an unhealthy {@link Result} with a descriptive
     *         error message or exception
     * @throws Exception if there is an unhandled error during the health check; this will result in a failed health check
     */
    protected abstract Result check() throws Exception;

    /**
     * Executes the health check. Any exceptions will result in an Unhealthy Result.
     * 
     * @return {@link Result} instance
     */
    public final Result execute() {
        try {
            return check();
        } catch (Exception e) {
            return Result.unhealthy(e);
        }
    }
}
