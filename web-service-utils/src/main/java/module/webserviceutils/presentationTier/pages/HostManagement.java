package module.webserviceutils.presentationTier.pages;

import java.util.Arrays;
import java.util.Map;

import module.vaadin.ui.BennuTheme;
import module.webserviceutils.domain.Host;
import module.webserviceutils.domain.HostSystem;
import module.webserviceutils.presentationTier.components.DefaultFieldFactory;

import org.apache.commons.lang.StringUtils;

import pt.ist.bennu.core.applicationTier.Authenticate.UserView;
import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.domain.RoleType;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.util.BundleUtil;
import pt.ist.vaadinframework.data.reflect.DomainContainer;
import pt.ist.vaadinframework.data.reflect.DomainItem;
import pt.ist.vaadinframework.ui.EmbeddedComponentContainer;
import pt.ist.vaadinframework.ui.GridSystemLayout;
import pt.ist.vaadinframework.ui.TransactionalForm;
import pt.ist.vaadinframework.ui.TransactionalTable;
import pt.utl.ist.fenix.tools.util.Strings;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

@SuppressWarnings("serial")
public abstract class HostManagement<T extends Host> extends CustomComponent implements EmbeddedComponentContainer {

    private GridSystemLayout gsl;
    private DomainItem<T> hostItem;
    private TransactionalForm hostForm;
    private Table table;
    private final DomainContainer<T> hostContainer;
    private final Object[] hostProperties;
    private final Class<T> hostClass;

    private final static DomainItem<HostSystem> systemItem;
    private final static String BUNDLE_NAME = "resources/WebServiceUtilsResources";

    static {
	systemItem = new DomainItem<HostSystem>(MyOrg.getInstance().getHostSystem(), HostSystem.class);
    }

    public HostManagement(final Class<T> hostClass, final String systemHostRelation, final Object[] hostProperties) {
	super();
	hostContainer = (DomainContainer<T>) systemItem.getItemProperty(systemHostRelation);
	hostContainer.setContainerProperties(Arrays.copyOf(hostProperties, hostProperties.length, String[].class));
	this.hostProperties = hostProperties;
	this.hostClass = hostClass;
	initUI();
    }

    private void initUI() {
	gsl = new GridSystemLayout();
	gsl.setMargin(Boolean.FALSE);
	updateUI();
	addNewItem();
	setCompositionRoot(gsl);
    }

    private void selectNext() {
	if (table.size() > 0) {
	    table.select(hostContainer.getIdByIndex(0));
	}
    }

    private void updateUI() {
	table = createHostsTable();
	gsl.setCell("header", 16, createHeaderTitle());
	gsl.setCell("host", 11, 5, 0, createHostButton());
	gsl.setCell("table", 11, table);
	gsl.setCell("form", 5, createHostForm());
	selectNext();
    }

    private Component createHeaderTitle() {
	final String title = BundleUtil.getStringFromResourceBundle(BUNDLE_NAME, "title." + hostClass.getSimpleName());
	final Label lblTitle = new Label(title);
	lblTitle.addStyleName(BennuTheme.LABEL_H2);
	return lblTitle;
    }

    private Component createHostButton() {
	final Button btNewHost = new Button("Adicionar Host", new Button.ClickListener() {

	    @Override
	    public void buttonClick(final ClickEvent event) {
		addNewItem();
	    }
	});
	return btNewHost;
    }

    private void setCurrentHostItem(final DomainItem<T> hostItem) {
	this.hostItem = hostItem;
	hostForm.setItemDataSource(hostItem);
	hostForm.setVisibleItemProperties(hostProperties);
    }

    private void addNewItem() {
	final DomainItem<T> item = hostContainer.addItem(hostClass);
	setCurrentHostItem(item);
    }

    @SuppressWarnings("serial")
    private Component createHostForm() {
	hostForm = new TransactionalForm(BUNDLE_NAME);
	hostForm.setSizeFull();
	hostForm.setCaption("Editar Host");
	hostForm.setImmediate(Boolean.TRUE);
	hostForm.setWriteThrough(Boolean.FALSE);
	hostForm.setFormFieldFactory(new DefaultFieldFactory(BUNDLE_NAME));
	hostForm.addButton("Submeter", new ClickListener() {

	    @Override
	    public void buttonClick(final ClickEvent event) {
		hostForm.commit();
		table.select(hostItem.getValue());
	    }

	});
	hostForm.addCancelButton();
	return hostForm;
    }

    private Table createHostsTable() {
	final Table table = new TransactionalTable(BUNDLE_NAME) {
	    @Override
	    protected String formatPropertyValue(final Object rowId, final Object colId, final Property property) {
		if (Strings.class.isAssignableFrom(property.getType())) {
		    final Strings value = (Strings) property.getValue();
		    if (value != null) {
			return StringUtils.join(value.toArray(), ";");
		    }
		    return StringUtils.EMPTY;
		}
		return super.formatPropertyValue(rowId, colId, property);
	    }
	};

	table.setSizeFull();
	table.setPageLength(0);

	table.setSelectable(Boolean.TRUE);
	table.setImmediate(Boolean.TRUE);
	table.setWriteThrough(Boolean.TRUE);
	table.setReadThrough(Boolean.TRUE);

	table.addListener(new ValueChangeListener() {

	    @Override
	    public void valueChange(final ValueChangeEvent event) {
		final Object value = event.getProperty().getValue();
		if (value != null) {
		    setCurrentHostItem((DomainItem<T>) table.getItem(value));
		}
	    }
	});

	table.setContainerDataSource(hostContainer);
	table.setVisibleColumns(hostProperties);

	table.addGeneratedColumn(StringUtils.EMPTY, new ColumnGenerator() {

	    @Override
	    public Object generateCell(final Table source, final Object itemId, final Object columnId) {
		final Button btRemove = new Button("Remover", new Button.ClickListener() {

		    @Override
		    public void buttonClick(final ClickEvent event) {
			source.getContainerDataSource().removeItem(itemId);
			((Host) itemId).delete();
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
    public void setArguments(final Map<String, String> arguments) {
    }

    @Override
    public boolean isAllowedToOpen(final Map<String, String> parameters) {
	final User user = UserView.getCurrentUser();
	return user != null && user.hasRoleType(RoleType.MANAGER);
    }

}
