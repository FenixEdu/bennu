package pt.ist.bennu.core.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import com.sun.jersey.api.core.ScanningResourceConfig;
import com.sun.jersey.core.spi.scanning.Scanner;
import com.sun.jersey.core.spi.scanning.ScannerException;
import com.sun.jersey.core.spi.scanning.ScannerListener;

public class BennuJerseyRestApplication extends ScanningResourceConfig {
    private static Set<Class<?>> types = new HashSet<>();

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
    }

    public static void registerEndpoint(Class<?> type) {
        types.add(type);
    }

}
