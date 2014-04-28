package org.fenixedu.bennu.spring;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.core.util.CoreConfiguration.ConfigurationProperties;
import org.fenixedu.bennu.spring.portal.PortalHandlerInterceptor;
import org.fenixedu.bennu.spring.portal.PortalHandlerMapping;
import org.fenixedu.commons.i18n.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

@Configuration
@ComponentScan("org.fenixedu.bennu")
public class BennuSpringConfiguration extends WebMvcConfigurationSupport {

    private static final Logger logger = LoggerFactory.getLogger(BennuSpringConfiguration.class);

    @Bean
    public ConfigurationProperties bennuCoreConfiguration() {
        return CoreConfiguration.getConfiguration();
    }

    @Bean
    public ViewResolver getViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setExposeContextBeansAsAttributes(true);
        resolver.setPrefix("/WEB-INF/");
        resolver.setSuffix(".jsp");
        resolver.setOrder(1_000_000);
        return resolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PortalHandlerInterceptor());
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public MessageSource messageSource(ApplicationContext context) {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setDefaultEncoding("UTF-8");
        final String[] baseNames = getBaseNames(context).toArray(new String[0]);
        logger.debug("Adding basenames by @BennuSpringModule configuration: {}", Arrays.toString(baseNames));
        source.setBasenames(baseNames);
        // Reload resources only when in development mode
        source.setCacheSeconds(bennuCoreConfiguration().developmentMode() ? 1 : -1);
        return source;
    }

    private Set<String> getBaseNames(ApplicationContext context) {
        final Set<String> baseNames = new HashSet<>();
        baseNames.add(getBundleBasename("BennuSpringResources"));
        final String[] beanNames = context.getBeanNamesForAnnotation(BennuSpringModule.class);
        for (String beanName : beanNames) {
            BennuSpringModule bennuSpringModuleAnnotation = context.findAnnotationOnBean(beanName, BennuSpringModule.class);
            if (bennuSpringModuleAnnotation != null) {
                final List<String> bundles = Arrays.asList(bennuSpringModuleAnnotation.bundles());
                baseNames.addAll(FluentIterable.from(bundles).transform(new Function<String, String>() {

                    @Override
                    public String apply(String bundle) {
                        return getBundleBasename(bundle);
                    }
                }).toSet());
            }
        }
        return baseNames;
    }

    private String getBundleBasename(String bundle) {
        return "/WEB-INF/resources/" + bundle;
    }

    @Bean
    public I18NBean i18n(MessageSource messageSource) {
        return new I18NBean(messageSource);
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new LocaleResolver() {

            @Override
            public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
                I18N.setLocale(request.getSession(), locale);
            }

            @Override
            public Locale resolveLocale(HttpServletRequest request) {
                return I18N.getLocale();
            }
        };
    }

    @Override
    public PortalHandlerMapping requestMappingHandlerMapping() {
        PortalHandlerMapping handlerMapping = new PortalHandlerMapping();
        handlerMapping.setOrder(0);
        handlerMapping.setInterceptors(getInterceptors());
        handlerMapping.setContentNegotiationManager(mvcContentNegotiationManager());
        return handlerMapping;
    }

    @Bean
    public ConversionService conversionService(GenericConversionService service) {
        service.addConverter(new DomainObjectConverter());
        return service;
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

}
