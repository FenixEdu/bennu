package module.webserviceutils.presentationTier.pages;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.vaadinframework.annotation.EmbeddedComponent;
import pt.ist.vaadinframework.data.reflect.DomainContainer;
import pt.ist.vaadinframework.data.reflect.DomainItem;
import pt.ist.vaadinframework.ui.EmbeddedComponentContainer;
import pt.ist.vaadinframework.ui.GridSystemLayout;
import pt.ist.vaadinframework.ui.TransactionalForm;
import pt.ist.vaadinframework.ui.TransactionalTable;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

import module.vaadin.ui.BennuTheme;
import module.webserviceutils.domain.WSURemoteHost;
import module.webserviceutils.domain.WSURemoteSystem;

@EmbeddedComponent(path = "WebServiceUtilsManagement")
public class WebServiceUtilsManagement extends CustomComponent implements EmbeddedComponentContainer {

    private GridSystemLayout gsl;
    private DomainItem<WSURemoteHost> hostItem;
    private TransactionalForm hostForm;
    private Table table;

    private final DomainContainer<WSURemoteHost> hostContainer;

    private final static DomainItem<WSURemoteSystem> systemItem;
    private final static String BUNDLE_NAME = "resources/WebServiceUtilsResources";
    private final static Object[] HOST_PROPERTIES = new Object[] { "name", "url", "username", "password", "allowInvocationAccess" };

    static {
	systemItem = new DomainItem<WSURemoteSystem>(MyOrg.getInstance().getRemoteSystem(), WSURemoteSystem.class);
    }

    public WebServiceUtilsManagement() {
	super();
	hostItem = new DomainItem<WSURemoteHost>(WSURemoteHost.class);
	hostContainer = (DomainContainer<WSURemoteHost>) systemItem.getItemProperty("remoteHosts");
	hostContainer.setContainerProperties("name", "url", "username", "password", "allowInvocationAccess");
	initUI();
    }

    private void initUI() {
	gsl = new GridSystemLayout();
	gsl.setMargin(Boolean.FALSE);
	updateUI();
	setCompositionRoot(gsl);
    }

    private void selectNext() {
	if (table.size() > 0) {
	    table.select(hostContainer.getIdByIndex(0));
	}
    }

    private void updateUI() {
	table = createHostsTable();
	gsl.setCell("host", 11, 5, 0, createHostButton());
	gsl.setCell("table", 11, table);
	gsl.setCell("form", 5, createHostForm());
	selectNext();
    }

    private Component createHostButton() {
	Button btNewHost = new Button("Adicionar Host", new Button.ClickListener() {

	    @Override
	    public void buttonClick(ClickEvent event) {
		addNewItem();
	    }
	});
	return btNewHost;
    }

    private void setCurrentHostItem(DomainItem<WSURemoteHost> hostItem) {
	this.hostItem = hostItem;
	hostForm.setItemDataSource(hostItem);
	hostForm.setVisibleItemProperties(HOST_PROPERTIES);
	hostForm.getField("allowInvocationAccess").setValue(Boolean.FALSE);
    }

    private void addNewItem() {
	setCurrentHostItem(hostContainer.addItem(WSURemoteHost.class));
    }

    private Component createHostForm() {
	hostForm = new TransactionalForm(BUNDLE_NAME);
	hostForm.setSizeFull();
	hostForm.setCaption("Editar Host");
	hostForm.setImmediate(Boolean.TRUE);
	hostForm.setWriteThrough(Boolean.FALSE);

	hostForm.setItemDataSource(hostItem);
	hostForm.setVisibleItemProperties(HOST_PROPERTIES);
	// hostForm.addButton("Submeter", new ClickListener() {
	//
	// @Override
	// public void buttonClick(ClickEvent event) {
	// hostForm.commit();
	// hostForm.setItemDataSource(hostItem);
	// hostForm.setVisibleItemProperties(HOST_PROPERTIES);
	// hostForm.getField("allowInvocationAccess").setValue(Boolean.FALSE);
	// hostItem.setValue(hostForm.getValue());
	// }
	// });
	hostForm.addButton("Submeter", new ClickListener() {

	    @Override
	    public void buttonClick(ClickEvent event) {
		hostForm.commit();
		table.select(hostItem.getValue());
	    }

	});
	hostForm.addCancelButton();
	return hostForm;
    }

    private Table createHostsTable() {
	final Table table = new TransactionalTable(BUNDLE_NAME);

	table.setSizeFull();
	table.setPageLength(0);

	table.setSelectable(Boolean.TRUE);
	table.setImmediate(Boolean.TRUE);
	table.setWriteThrough(Boolean.TRUE);
	table.setReadThrough(Boolean.TRUE);

	table.addListener(new ValueChangeListener() {

	    @Override
	    public void valueChange(ValueChangeEvent event) {
		final Object value = event.getProperty().getValue();
		if (value != null) {
		    setCurrentHostItem((DomainItem<WSURemoteHost>) table.getItem(value));
		}
	    }
	});

	table.setContainerDataSource(hostContainer);
	table.setVisibleColumns(HOST_PROPERTIES);

	table.addGeneratedColumn(StringUtils.EMPTY, new ColumnGenerator() {

	    @Override
	    public Object generateCell(final Table source, final Object itemId, final Object columnId) {
		Button btRemove = new Button("Remover", new Button.ClickListener() {

		    @Override
		    public void buttonClick(ClickEvent event) {
			source.getContainerDataSource().removeItem(itemId);
			((WSURemoteHost) itemId).delete();
			selectNext();
		    }

		});
		btRemove.addStyleName(BennuTheme.BUTTON_LINK);
		return btRemove;
	    }
	});

	return table;
    }

    @Override
    public void setArguments(Map<String, String> arguments) {
    }

    @Override
    public boolean isAllowedToOpen(Map<String, String> parameters) {
	return true;
    }

}
