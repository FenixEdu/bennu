package org.fenixedu.bennuAdmin.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContainerInitializer;

public class BennuAdminInitializer implements ServletContainerInitializer {
  private static final Logger LOG = LoggerFactory.getLogger(BennuAdminInitializer.class);

  @Override
  public void onStartup(java.util.Set<Class<?>> c, javax.servlet.ServletContext ctx) {
    LOG.info("Initializing Bennu Admin.");
  }
}
