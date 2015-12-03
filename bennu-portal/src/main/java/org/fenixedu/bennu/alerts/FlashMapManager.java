package org.fenixedu.bennu.alerts;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

class FlashMapManager {

    private static final Object DEFAULT_FLASH_MAPS_MUTEX = new Object();

    private static final String FLASH_MAPS_SESSION_ATTRIBUTE = FlashMapManager.class.getName() + ".FLASH_MAPS";

    private static final String SESSION_MUTEX_ATTRIBUTE = FlashMapManager.class.getName() + ".MUTEX";

    protected final Logger logger = LoggerFactory.getLogger(FlashMapManager.class);

    private int flashMapTimeout = 180;

    private void setFlashMapTimeout(int flashMapTimeout) {
        this.flashMapTimeout = flashMapTimeout;
    }

    private int getFlashMapTimeout() {
        return this.flashMapTimeout;
    }


    /**
     * Looks into the current request and tries to find FlashMaps that match the current request
     *
     * @param request the current request
     * @return a FlashMap that matches the current request
     */
    public final FlashMap retrieveAndUpdate(HttpServletRequest request) {
        List<FlashMap> allFlashMaps = retrieveFlashMaps(request);
        if (allFlashMaps == null || allFlashMaps.isEmpty()) {
            return null;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Retrieved FlashMap(s): " + allFlashMaps);
        }
        List<FlashMap> mapsToRemove = getExpiredFlashMaps(allFlashMaps);
        FlashMap match = getMatchingFlashMap(allFlashMaps, request);
        if (match != null) {
            mapsToRemove.add(match);
        }

        if (!mapsToRemove.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Removing FlashMap(s): " + mapsToRemove);
            }
            Object mutex = getFlashMapsMutex(request);
            if (mutex != null) {
                synchronized (mutex) {
                    allFlashMaps = retrieveFlashMaps(request);
                    if (allFlashMaps != null) {
                        allFlashMaps.removeAll(mapsToRemove);
                        updateFlashMaps(allFlashMaps, request);
                    }
                }
            } else {
                allFlashMaps.removeAll(mapsToRemove);
                updateFlashMaps(allFlashMaps, request);
            }
        }

        return match;
    }

    private List<FlashMap> getExpiredFlashMaps(List<FlashMap> allMaps) {
        List<FlashMap> result = new LinkedList<FlashMap>();
        for (FlashMap map : allMaps) {
            if (map.isExpired()) {
                result.add(map);
            }
        }
        return result;
    }

    private FlashMap getMatchingFlashMap(List<FlashMap> allMaps, HttpServletRequest request) {
        List<FlashMap> result = new LinkedList<FlashMap>();
        for (FlashMap flashMap : allMaps) {
            if (isFlashMapForRequest(flashMap, request)) {
                result.add(flashMap);
            }
        }
        if (!result.isEmpty()) {
            Collections.sort(result);
            if (logger.isDebugEnabled()) {
                logger.debug("Found matching FlashMap(s): " + result);
            }
            return result.get(0);
        }
        return null;
    }


    /**
     * Helper method to process and transform a query string into a {@link com.google.common.collect.Multimap}.
     *
     * @param query the query String
     * @return the parameters in a Multimap
     */
    protected Multimap<String, String> splitQuery(String query) {
        try {
            final Multimap<String, String> query_pairs = ArrayListMultimap.create();
            if (query != null) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    final int idx = pair.indexOf("=");
                    final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
                    final String value =
                            idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
                    query_pairs.put(key, value);
                }
            }
            return query_pairs;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isFlashMapForRequest(FlashMap flashMap, HttpServletRequest request) {
        String expectedPath = flashMap.getTargetRequestPath();
        if (expectedPath != null) {
            String requestUri = getOriginatingRequestUri(request);
            if (!requestUri.equals(expectedPath) && !requestUri.equals(expectedPath + "/")) {
                return false;
            }
        }

        Multimap<String, String> actualParams = splitQuery(request.getQueryString());
        Multimap<String, String> expectedParams = flashMap.getTargetRequestParams();
        for (String expectedName : expectedParams.keySet()) {
            Collection<String> actualValues = actualParams.get(expectedName);
            if (actualValues == null) {
                return false;
            }
            for (String expectedValue : expectedParams.get(expectedName)) {
                if (!actualValues.contains(expectedValue)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Saves the current FlashMap
     *
     * @param flashMap FlashMap to attach
     * @param request the current request
     */
    public final void saveOutputFlashMap(FlashMap flashMap, HttpServletRequest request) {
        if (flashMap == null || flashMap.isEmpty()) {
            return;
        }

        String path = decodeAndNormalizePath(flashMap.getTargetRequestPath(), request);
        flashMap.setTargetRequestPath(path);

        if (logger.isDebugEnabled()) {
            logger.debug("Saving FlashMap=" + flashMap);
        }
        flashMap.startExpirationPeriod(getFlashMapTimeout());

        Object mutex = getFlashMapsMutex(request);
        if (mutex != null) {
            synchronized (mutex) {
                List<FlashMap> allFlashMaps = retrieveFlashMaps(request);
                allFlashMaps = (allFlashMaps != null ? allFlashMaps : new CopyOnWriteArrayList<FlashMap>());
                allFlashMaps.add(flashMap);
                updateFlashMaps(allFlashMaps, request);
            }
        } else {
            List<FlashMap> allFlashMaps = retrieveFlashMaps(request);
            allFlashMaps = (allFlashMaps != null ? allFlashMaps : new LinkedList<FlashMap>());
            allFlashMaps.add(flashMap);
            updateFlashMaps(allFlashMaps, request);
        }
    }

    private String decodeRequestString(HttpServletRequest request, String source) {
        return decodeInternal(request, source);
    }

    private String determineEncoding(HttpServletRequest request) {
        String enc = request.getCharacterEncoding();
        if (enc == null) {
            enc = "ISO-8859-1";
        }
        return enc;
    }

    private String decodeInternal(HttpServletRequest request, String source) {
        String enc = determineEncoding(request);
        try {
            return URLDecoder.decode(source, enc);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private String decodeAndNormalizePath(String path, HttpServletRequest request) {
        if (path != null) {
            path = decodeRequestString(request, path);
            if (path.charAt(0) != '/') {
                String requestUri = getRequestUri(request);
                path = requestUri.substring(0, requestUri.lastIndexOf('/') + 1) + path;
                path = Paths.get(path).normalize().toString();
            }
        }
        return path;
    }

    private String getOriginatingRequestUri(HttpServletRequest request) {
        String uri = (String) request.getAttribute("javax.servlet.forward.request_uri");
        if (uri == null) {
            uri = request.getRequestURI();
        }

        return decodeAndCleanUriString(request, uri);
    }

    private String decodeAndCleanUriString(HttpServletRequest request, String uri) {
        uri = removeSemicolonContent(uri);
        uri = decodeRequestString(request, uri);
        return uri;
    }

    private String removeSemicolonContent(String requestUri) {
        return removeSemicolonContentInternal(requestUri);

    }

    private String removeJsessionid(String requestUri) {
        int startIndex = requestUri.toLowerCase().indexOf(";jsessionid=");
        if (startIndex != -1) {
            int endIndex = requestUri.indexOf(';', startIndex + 12);
            String start = requestUri.substring(0, startIndex);
            requestUri = (endIndex != -1) ? start + requestUri.substring(endIndex) : start;
        }
        return requestUri;
    }

    private String removeSemicolonContentInternal(String requestUri) {
        int semicolonIndex = requestUri.indexOf(';');
        while (semicolonIndex != -1) {
            int slashIndex = requestUri.indexOf('/', semicolonIndex);
            String start = requestUri.substring(0, semicolonIndex);
            requestUri = (slashIndex != -1) ? start + requestUri.substring(slashIndex) : start;
            semicolonIndex = requestUri.indexOf(';', semicolonIndex);
        }
        return requestUri;
    }

    private String getRequestUri(HttpServletRequest request) {
        String uri = (String) request.getAttribute("javax.servlet.include.request_uri");
        if (uri == null) {
            uri = request.getRequestURI();
        }
        return decodeAndCleanUriString(request, uri);
    }

    private List<FlashMap> retrieveFlashMaps(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null ? (List<FlashMap>) session.getAttribute(FLASH_MAPS_SESSION_ATTRIBUTE) : null);
    }

    private void updateFlashMaps(List<FlashMap> flashMaps, HttpServletRequest request) {
        if (flashMaps.isEmpty()) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(FLASH_MAPS_SESSION_ATTRIBUTE);
            }
        } else {
            request.getSession().setAttribute(FLASH_MAPS_SESSION_ATTRIBUTE, flashMaps);
        }
    }

    private Object getFlashMapsMutex(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object mutex = session.getAttribute(SESSION_MUTEX_ATTRIBUTE);
        if (mutex == null) {
            mutex = session;
        }
        return mutex;
    }

}
