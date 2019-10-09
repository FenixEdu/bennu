package org.fenixedu.bennu.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Helper class to access Spring WebApplicationContext and Managed beans
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
@Component
public class BennuSpringContextHelper {

    private static WebApplicationContext webApplicationContext;

    @Autowired
    public BennuSpringContextHelper(WebApplicationContext webApplicationContext) {
        BennuSpringContextHelper.webApplicationContext = webApplicationContext;
    }

    public static WebApplicationContext getWebApplicationContext() {
        return webApplicationContext;
    }

    public static <T extends Object> T getBean(Class<T> beanClass) {
        return webApplicationContext.getBean(beanClass);
    }
}