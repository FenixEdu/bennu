package org.fenixedu.bennu.core.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.core.ScanningResourceConfig;
import com.sun.jersey.core.spi.scanning.Scanner;
import com.sun.jersey.core.spi.scanning.ScannerException;
import com.sun.jersey.core.spi.scanning.ScannerListener;

public class BennuJerseyRestApplication extends ScanningResourceConfig {
    private static Set<Class<?>> types = new HashSet<>();
    private static Map<String, Object> modulePathResource = new HashMap<>();
    private static Logger LOG = LoggerFactory.getLogger(BennuJerseyRestApplication.class);

    public BennuJerseyRestApplication() {
        super();
        init(new Scanner() {
            @Override
            public void scan(ScannerListener sl) throws ScannerException {
                for (Class<?> type : types) {
                    try (InputStream stream =
                            Thread.currentThread().getContextClassLoader()
                                    .getResourceAsStream(type.getName().replaceAll("\\.", "/") + ".class")) {
                        sl.onProcess(type.getName(), stream);
                    } catch (IOException ex) {
                        throw new ScannerException("IO error when scanning type: " + type.getName(), ex);
                    }
                }
            }
        });
        LOG.debug("Registering Bennu REST entrypoints:");
    }

    public static void register(Class<?> type) {
        types.add(type);
    }

    @Override
    public Map<String, Object> getExplicitRootResources() {
        return modulePathResource;
    }

    public static void registerEndpoint(String moduleName, String path, Class<?> type) {
        final String completePath = path.startsWith("/") ? moduleName.concat(path) : moduleName.concat("/").concat(path);
        modulePathResource.put(completePath, type);
        LOG.debug("\t{} mapped on '{}'", type.getName(), completePath);
    }

}