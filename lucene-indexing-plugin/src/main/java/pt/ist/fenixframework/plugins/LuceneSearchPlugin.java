package pt.ist.fenixframework.plugins;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import pt.ist.fenixframework.FenixFrameworkPlugin;
import pt.ist.fenixframework.plugins.luceneIndexing.IndexListener;
import pt.ist.fenixframework.plugins.luceneIndexing.domain.LuceneSearchPluginRoot;
import pt.ist.fenixframework.pstm.TopLevelTransaction;

public class LuceneSearchPlugin implements FenixFrameworkPlugin {
    public static final Logger LOGGER = Logger.getLogger(LuceneSearchPlugin.class.getName());

    @Override
    public List<URL> getDomainModel() {
	URL resource = getClass().getResource("/luceneSearch-plugin.dml");
	return Collections.singletonList(resource);
    }

    @Override
    public void initialize() {
	LuceneSearchPluginRoot.getInstance();
	TopLevelTransaction.addCommitListener(new IndexListener());
    }
}
