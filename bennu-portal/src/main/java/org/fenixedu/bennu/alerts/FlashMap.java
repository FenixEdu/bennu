package org.fenixedu.bennu.alerts;

import java.util.HashMap;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * A FlashMap provides a way for one request to store attributes intended for
 * use in another. This is most commonly needed when redirecting from one URL
 * to another -- e.g. the Post/Redirect/Get pattern. A FlashMap is saved before
 * the redirect (typically in the session) and is made available after the
 * redirect and removed immediately.
 *
 * <p>A FlashMap can be set up with a request path and request parameters to
 * help identify the target request. Without this information, a FlashMap is
 * made available to the next request, which may or may not be the intended
 * recipient. On a redirect, the target URL is known and a FlashMap can be
 * updated with that information. This is done automatically when the
 * a 3** response is used.
 *
 * See {@link org.fenixedu.bennu.alerts.RedirectAttributes}
 * for an overview of using flash attributes in annotated controllers.
 *
 * @author Artur Ventura (artur.ventura@tecnico.pt)
 * @since 3.5.0
 */
final class FlashMap extends HashMap<String, Object> implements Comparable<FlashMap> {

    private String targetRequestPath;
    private final Multimap<String, String> targetRequestParams = ArrayListMultimap.create();

    private long expirationTime = -1;

    /**
     * Provide a URL path to help identify the target request for this FlashMap.
     * <p>
     * The path may be absolute (e.g. "/application/resource") or relative to the current request (e.g. "../resource").
     * 
     * @param path the path
     */
    public void setTargetRequestPath(String path) {
        this.targetRequestPath = path;
    }

    /**
     * Return the target URL path (or {@code null} if none specified).
     * 
     * @return the path
     */
    public String getTargetRequestPath() {
        return this.targetRequestPath;
    }

    /**
     * Provide request parameters identifying the request for this FlashMap.
     * 
     * @param params a Map with the names and values of expected parameters
     */
    public FlashMap addTargetRequestParams(Multimap<String, String> params) {
        if (params != null) {
            for (String key : params.keySet()) {
                for (String value : params.get(key)) {
                    addTargetRequestParam(key, value);
                }
            }
        }
        return this;
    }

    /**
     * Provide a request parameter identifying the request for this FlashMap.
     * 
     * @param name the expected parameter name (skipped if empty or {@code null})
     * @param value the expected value (skipped if empty or {@code null})
     * @return the FlashMap
     */
    public FlashMap addTargetRequestParam(String name, String value) {
        if (!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(value)) {
            this.targetRequestParams.put(name, value);
        }
        return this;
    }

    /**
     * Return the parameters identifying the target request, or an empty map.
     * 
     * @return the parameters
     */
    public Multimap<String, String> getTargetRequestParams() {
        return this.targetRequestParams;
    }

    /**
     * Start the expiration period for this instance.
     * 
     * @param timeToLive the number of seconds before expiration
     */
    public void startExpirationPeriod(int timeToLive) {
        this.expirationTime = System.currentTimeMillis() + timeToLive * 1000;
    }

    /**
     * Set the expiration time for the FlashMap. This is provided for serialization
     * purposes but can also be used instead {@link #startExpirationPeriod(int)}
     * 
     * @param expirationTime the expirationTime
     */
    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * Return the expiration time for the FlashMap or -1 if the expiration
     * period has not started.
     * 
     * @return the expiration time;
     */
    public long getExpirationTime() {
        return this.expirationTime;
    }

    /**
     * Return whether this instance has expired depending on the amount of
     * elapsed time since the call to {@link #startExpirationPeriod}.
     */
    public boolean isExpired() {
        return (this.expirationTime != -1 && System.currentTimeMillis() > this.expirationTime);
    }

    /**
     * Compare two FlashMaps and prefer the one that specifies a target URL
     * path or has more target URL parameters. Before comparing FlashMap
     * instances ensure that they match a given request.
     */
    @Override
    public int compareTo(FlashMap other) {
        int thisUrlPath = (this.targetRequestPath != null ? 1 : 0);
        int otherUrlPath = (other.targetRequestPath != null ? 1 : 0);
        if (thisUrlPath != otherUrlPath) {
            return otherUrlPath - thisUrlPath;
        } else {
            return other.targetRequestParams.size() - this.targetRequestParams.size();
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FlashMap)) {
            return false;
        }
        FlashMap otherFlashMap = (FlashMap) other;
        return (super.equals(otherFlashMap) && Objects.equal(this.targetRequestPath, otherFlashMap.targetRequestPath) && this.targetRequestParams
                .equals(otherFlashMap.targetRequestParams));
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(this.targetRequestPath);
        result = 31 * result + this.targetRequestParams.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FlashMap [attributes=" + super.toString() + ", targetRequestPath=" + this.targetRequestPath
                + ", targetRequestParams=" + this.targetRequestParams + "]";
    }

}
