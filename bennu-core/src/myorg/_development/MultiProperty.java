package myorg._development;

import java.util.Properties;

public class MultiProperty extends Properties {

    @Override
    public synchronized Object put(final Object key, final Object value) {
	final StringBuilder result = new StringBuilder();
	final String property = getProperty((String) key);
	addValues(result, property);
	addValues(result, (String) value);
	return result.toString();
    }

    private void addValues(final StringBuilder result, final String value) {
	if (value != null && !value.isEmpty()) {
	    final String[] properties = value.split(",");
	    for (final String property : properties) {
		final String trimmed = property.trim();
		if (result.indexOf(trimmed) < 0) {
		    result.append(",");
		    result.append(trimmed);
		}
	    }
	}	
    }

}
