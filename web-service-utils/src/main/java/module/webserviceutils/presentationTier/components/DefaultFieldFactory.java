package module.webserviceutils.presentationTier.components;

import pt.utl.ist.fenix.tools.util.Strings;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

public class DefaultFieldFactory extends pt.ist.vaadinframework.ui.DefaultFieldFactory {

    public DefaultFieldFactory(final String bundlename) {
        super(bundlename);
    }

    @Override
    protected Field makeField(final Item item, final Object propertyId, final Component uiContext) {
        final Property itemProperty = item.getItemProperty(propertyId);
        final Class<?> type = itemProperty.getType();
        if (Strings.class.isAssignableFrom(type)) {
            return new StringsEditor();
        }
        return super.makeField(item, propertyId, uiContext);
    }

}
