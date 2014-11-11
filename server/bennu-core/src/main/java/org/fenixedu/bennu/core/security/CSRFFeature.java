package org.fenixedu.bennu.core.security;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Stream;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

/***
 * 
 * Apply {@link CSRFApiProtectionFilter} if resourceMethod is annotated with {@link POST}, {@link PUT} or {@link DELETE}
 * 
 * @see CoreConfiguration.ConfigurationProperties#apiCSRFFilterEnabled()
 * 
 * @author Sérgio Silva (sergio.silva@tecnico.ulisboa.pt)
 *
 */
@Provider
public class CSRFFeature implements DynamicFeature {
    private static final Logger logger = LoggerFactory.getLogger(CSRFFeature.class);

    private static final CSRFApiProtectionFilter filter;
    private static final Set<Class<? extends Annotation>> toFilterAnnotations;

    static {
        if (CoreConfiguration.getConfiguration().apiCSRFFilterEnabled()) {
            logger.info("Enabling CSRF filter for POST, PUT and DELETE /api endpoints.");
            logger.debug("CSRF protected endpoints:");
            filter = new CSRFApiProtectionFilter();
            toFilterAnnotations = ImmutableSet.<Class<? extends Annotation>> of(POST.class, PUT.class, DELETE.class);
        } else {
            logger.warn("CSRF filter for POST, PUT and DELETE /api endpoints is DISABLED!. You may be vulnerable to CSRF attacks.");
            filter = null;
            toFilterAnnotations = null;
        }
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        if (filter != null) {
            if (Stream.of(resourceInfo.getResourceMethod().getAnnotations()).map(Annotation::annotationType)
                    .anyMatch(toFilterAnnotations::contains)) {
                logger.debug("\t{}#{}", resourceInfo.getResourceClass().getSimpleName(), resourceInfo.getResourceMethod()
                        .getName());
                context.register(filter);
            }
        }
    }
}
