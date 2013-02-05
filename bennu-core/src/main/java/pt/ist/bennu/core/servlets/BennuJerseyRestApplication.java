package pt.ist.bennu.core.servlets;

import pt.ist.bennu.core.util.ConfigurationManager;

import com.sun.jersey.api.core.PackagesResourceConfig;

public class BennuJerseyRestApplication extends PackagesResourceConfig {

    public BennuJerseyRestApplication() {
        super(ConfigurationManager.getRestRootClassPackages());
    }

}