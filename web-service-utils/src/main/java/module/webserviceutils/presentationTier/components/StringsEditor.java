package module.webserviceutils.presentationTier.components;

import org.apache.commons.lang.StringUtils;
import org.vaadin.customfield.PropertyConverter;

import pt.utl.ist.fenix.tools.util.Strings;

import com.vaadin.data.Property;
import com.vaadin.ui.TextArea;

public class StringsEditor extends TextArea {

    private static final long serialVersionUID = 1L;

    class StringsPropertyConverter extends PropertyConverter<Strings, String> {

	private static final long serialVersionUID = 1L;

	public StringsPropertyConverter(final Property propertyDataSource) {
	    super(propertyDataSource);
	}

	@Override
	public String format(final Strings propertyValue) {
	    final Object[] array = propertyValue.toArray();
	    if (array.length == 0) {
		return StringUtils.EMPTY;
	    }
	    return StringUtils.join(array, ";");
	}

	@Override
	public Strings parse(final String fieldValue) throws ConversionException {
	    return new Strings(fieldValue.split(";"));
	}

    };

    @Override
    public void setPropertyDataSource(final Property newDataSource) {
	super.setPropertyDataSource(new StringsPropertyConverter(newDataSource));
    }

}
