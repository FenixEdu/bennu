package org.fenixedu.bennu;

import org.fenixedu.bennu.spring.BennuSpringModule;
import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;

@BennuSpringModule(basePackages = "org.fenixedu.bennuAdmin", bundles = "BennuAdminResources")
public class BennuAdminConfiguration {
  public static final String BUNDLE = "resources.BennuAdminResources";

  @ConfigurationManager(description = "Bennu Admin Configuration")
  public interface ConfigurationProperties {}

  public static ConfigurationProperties getConfiguration() {
    return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
  }
}
