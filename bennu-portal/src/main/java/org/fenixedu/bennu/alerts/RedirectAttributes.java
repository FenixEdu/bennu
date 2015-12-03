package org.fenixedu.bennu.alerts;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class exposes the main API for using FlashMap. FlashMap are Maps that
 * are only available for a short time, that requests can use to store
 * information between requests. It is particularly useful for POST/GET and
 * POST/Redirect/GET scenarios where a message should be presented to the user.
 *
 * <p>
 * Each request has available two FlashMaps available, an input and an output.
 * The input FlashMap is set of objects defined on previous request, and is read
 * only. The output FlashMap can be use to set attributes to be used on
 * subsequent requests. FlashMaps are removed after a timeout (180 seconds by
 * default) if not used.
 * </p>
 *
 * @author Artur Ventura (artur.ventura@tecnico.pt)
 * @since 3.5.0
 * @see org.fenixedu.bennu.alerts.FlashMap
 */
class RedirectAttributes {
	private static final String INPUT_FLASH_MAP_ATTRIBUTE = RedirectAttributes.class
			.getName() + ".INPUT_FLASH_MAP";

	private static final String OUTPUT_FLASH_MAP_ATTRIBUTE = RedirectAttributes.class
			.getName() + ".OUTPUT_FLASH_MAP";

	private static final String FLASH_MAP_MANAGER_ATTRIBUTE = RedirectAttributes.class
			.getName() + ".FLASH_MAP_MANAGER";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(RedirectAttributes.class);

	/**
	 * Finds a matching input FlashMap and places it on the request as a
	 * attribute.
	 *
	 * @param request
	 *            the current request
	 */
	protected static void initFromRequest(HttpServletRequest request) {
		if (request.getAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE) == null) {
			FlashMapManager sfmm = new FlashMapManager();

			FlashMap inputFlashMap = sfmm.retrieveAndUpdate(request);
			if (inputFlashMap != null) {
				request.setAttribute(INPUT_FLASH_MAP_ATTRIBUTE,
						Collections.unmodifiableMap(inputFlashMap));
			}
			request.setAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
			request.setAttribute(FLASH_MAP_MANAGER_ATTRIBUTE, sfmm);
		}
	}

	/**
	 * Prepares the FlashMap to be saved, for a next request. If the response is
	 * a 3** code, and we have a next location available, the FlashpMap is
	 * tagged to only be available on that specific URL. This prevents the case
	 * of some other page hijacks the FlashMap. This is particularly useful on
	 * POST/Redirect/GET scenario.
	 *
	 * @param request
	 *            the current request
	 * @param response
	 *            the current response
	 */
	protected static void flush(HttpServletRequest request,
			HttpServletResponse response) {
		FlashMap flashMap = (FlashMap) request
				.getAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE);
		if (flashMap != null && !flashMap.isEmpty()) {
			FlashMapManager flashMapManager = getFlashMapManager(request);
			int status = response.getStatus();
			if ((status == HttpServletResponse.SC_MULTIPLE_CHOICES
					|| status == HttpServletResponse.SC_MOVED_PERMANENTLY
					|| status == HttpServletResponse.SC_FOUND
					|| status == HttpServletResponse.SC_SEE_OTHER || status == HttpServletResponse.SC_TEMPORARY_REDIRECT)
					&& response.getHeader("Location") != null) {
				URI url;
				try {
					url = new URI(response.getHeader("Location"));
					flashMap.setTargetRequestPath(url.getPath());
					flashMap.addTargetRequestParams(flashMapManager
							.splitQuery(url.getQuery()));
				} catch (URISyntaxException e) {
					LOGGER.error("problem in ");
				}

			}
			flashMapManager.saveOutputFlashMap(flashMap, request);
		}

	}

	/**
	 * Returns the Input FlashMap for this request.
	 *
	 * @param request
	 *            the current request
	 * @return an Map with the available attributes.
	 */
	public static Map<String, ?> getInputFlashMap(HttpServletRequest request) {
		Object obj = request.getAttribute(INPUT_FLASH_MAP_ATTRIBUTE);

		if (obj != null) {
			return (Map<String, ?>) obj;
		} else {
			FlashMap inputFlashMap = new FlashMapManager()
					.retrieveAndUpdate(request);
			if (inputFlashMap != null) {
				Map<String, ?> map = Collections.unmodifiableMap(inputFlashMap);
				request.setAttribute(INPUT_FLASH_MAP_ATTRIBUTE, map);
				return inputFlashMap;
			} else {
				return Collections.emptyMap();
			}
		}
	}

	/**
	 * Returns the Output FlashMap for this request. Use this method to set
	 * stuff for the next request.
	 *
	 * @param request
	 *            the current request
	 * @return the Output FlashMap
	 */
	public static FlashMap getOutputFlashMap(HttpServletRequest request) {
		return (FlashMap) request.getAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE);
	}

	/**
	 * Returns the FlashMapManager for this request
	 *
	 * @param request
	 *            the current request
	 * @return a FlashMapManager for this instance
	 */
	public static FlashMapManager getFlashMapManager(HttpServletRequest request) {
		return (FlashMapManager) request
				.getAttribute(FLASH_MAP_MANAGER_ATTRIBUTE);
	}

	/**
	 * Adds a attribute to the output FlashMap.
	 *
	 * @param request
	 *            the current request
	 * @param key
	 *            the key
	 * @param object
	 *            the value
	 */
	public static void addFlashAttribute(HttpServletRequest request,
			String key, Object object) {
		getOutputFlashMap(request).put(key, object);
	}

	/**
	 * ADds a attribute to the output FlashMap, using the object class name as
	 * key.
	 *
	 * @param request
	 *            the current request
	 * @param object
	 *            the value
	 */
	public static void addFlashAttribute(HttpServletRequest request,
			Object object) {
		getOutputFlashMap(request).put(object.getClass().getCanonicalName(),
				object);
	}

}
