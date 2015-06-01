package org.fenixedu.bennu.spring.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.spring.BennuSpringConfiguration;
import org.fenixedu.bennu.spring.security.CSRFTokenTest.MyConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BennuSpringConfiguration.class, MyConfig.class })
public class CSRFTokenTest {

    private static final CSRFToken TEST_TOKEN = new CSRFToken("RANDOM_TOKEN");

    public static class MyConfig {

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        public CSRFTokenRepository csrfTokenRepository() {
            return new CSRFTokenRepository() {
                @Override
                public CSRFToken getToken(HttpServletRequest request) {
                    return TEST_TOKEN;
                }
            };
        }

    }

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    // GET

    @Test
    public void testGetRequestsAreNotAffected() throws Exception {
        this.mockMvc.perform(get("/test/csrf")).andExpect(status().isOk());
    }

    // POST

    @Test
    public void testPOSTRequestsAreProperlyFiltered() throws Exception {
        this.mockMvc.perform(post("/test/csrf")).andExpect(status().isBadRequest());
    }

    @Test
    public void testPOSTRequestsWithWrongToken() throws Exception {
        this.mockMvc.perform(post("/test/csrf").param(TEST_TOKEN.getParameterName(), "WRONG_TOKEN")).andExpect(
                status().isBadRequest());
    }

    @Test
    public void testPOSTRequestsWithParameterWorks() throws Exception {
        this.mockMvc.perform(post("/test/csrf").param(TEST_TOKEN.getParameterName(), TEST_TOKEN.getToken())).andExpect(
                status().isOk());
    }

    @Test
    public void testPOSTRequestsWithHeaderWorks() throws Exception {
        this.mockMvc.perform(post("/test/csrf").header(TEST_TOKEN.getHeaderName(), TEST_TOKEN.getToken())).andExpect(
                status().isOk());
    }

    @Test
    public void testPUTRequestsWithIgnoreAnnotationAreNotAffected() throws Exception {
        this.mockMvc.perform(post("/test/csrf-ignored")).andExpect(status().isOk());
    }

    // POST

    @Test
    public void testPUTRequestsAreProperlyFiltered() throws Exception {
        this.mockMvc.perform(put("/test/csrf")).andExpect(status().isBadRequest());
    }

    @Test
    public void testPUTRequestsWithWrongToken() throws Exception {
        this.mockMvc.perform(put("/test/csrf").param(TEST_TOKEN.getParameterName(), "WRONG_TOKEN")).andExpect(
                status().isBadRequest());
    }

    @Test
    public void testPUTRequestsWithParameterWorks() throws Exception {
        this.mockMvc.perform(put("/test/csrf").param(TEST_TOKEN.getParameterName(), TEST_TOKEN.getToken())).andExpect(
                status().isOk());
    }

    @Test
    public void testPUTRequestsWithHeaderWorks() throws Exception {
        this.mockMvc.perform(put("/test/csrf").header(TEST_TOKEN.getHeaderName(), TEST_TOKEN.getToken())).andExpect(
                status().isOk());
    }

    @Test
    public void testPOSTRequestsWithIgnoreAnnotationAreNotAffected() throws Exception {
        this.mockMvc.perform(put("/test/csrf-ignored")).andExpect(status().isOk());
    }

    // DELETE

    @Test
    public void testDELETERequestsAreProperlyFiltered() throws Exception {
        this.mockMvc.perform(delete("/test/csrf")).andExpect(status().isBadRequest());
    }

    @Test
    public void testDELETERequestsWithWrongToken() throws Exception {
        this.mockMvc.perform(delete("/test/csrf").param(TEST_TOKEN.getParameterName(), "WRONG_TOKEN")).andExpect(
                status().isBadRequest());
    }

    @Test
    public void testDELETERequestsWithParameterWorks() throws Exception {
        this.mockMvc.perform(delete("/test/csrf").param(TEST_TOKEN.getParameterName(), TEST_TOKEN.getToken())).andExpect(
                status().isOk());
    }

    @Test
    public void testDELETERequestsWithHeaderWorks() throws Exception {
        this.mockMvc.perform(delete("/test/csrf").header(TEST_TOKEN.getHeaderName(), TEST_TOKEN.getToken())).andExpect(
                status().isOk());
    }

    @Test
    public void testDELETERequestsWithIgnoreAnnotationAreNotAffected() throws Exception {
        this.mockMvc.perform(delete("/test/csrf-ignored")).andExpect(status().isOk());
    }

}
