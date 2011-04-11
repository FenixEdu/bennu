package myorg._development;

import java.util.Properties;

public class MultiProperty extends Properties {

    @Override
    public synchronized Object put(final Object key, final Object value) {
	final String property = getProperty((String) key);
	final String putValue;
	if (property != null && !property.isEmpty()) {
	    putValue = ((String) value) + ',' + property;
	} else {
	    putValue = (String) value;
	}
        return super.put(key, putValue);
    }

}
