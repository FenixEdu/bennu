package pt.ist.bennu.core.test;

import jvstm.TransactionalCommand;
import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.rest.JerseyClient;
import pt.ist.bennu.core.util.ConfigurationManager;
import pt.ist.bennu.core.util.rest.RestHost;
import pt.ist.fenixframework.FenixFrameworkInitializer;
import pt.ist.fenixframework.pstm.PersistentRoot;
import pt.ist.fenixframework.pstm.Transaction;

//@RunWith(JUnit4.class)
public class TestJerseyClient {

    public static void setup() {
        try {
            Class.forName(FenixFrameworkInitializer.class.getName());
        } catch (ClassNotFoundException e) {
        }
        PersistentRoot.initRootIfNeeded(ConfigurationManager.getFenixFrameworkConfig());
        ensureModelBootstrap();
    }

    private static void ensureModelBootstrap() {
        Transaction.withTransaction(false, new TransactionalCommand() {
            @Override
            public void doIt() {
                if (!Bennu.getInstance().hasAnyVirtualHosts()) {
                    new VirtualHost("localhost");
                }
            }
        });

    }

    //@Test
    public void test() {
        final RestHost host = ConfigurationManager.getHost("fenix");
        final JerseyClient client = host.getClient();
        try {
            final String result = client.method("orders").get("ist152416");
            System.out.println(result);
        } catch (Throwable e) {
        }
    }

}
