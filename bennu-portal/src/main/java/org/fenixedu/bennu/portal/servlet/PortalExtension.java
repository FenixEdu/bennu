package org.fenixedu.bennu.portal.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fenixedu.bennu.core.i18n.BundleUtil;

import com.google.common.io.BaseEncoding;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.Test;

class PortalExtension extends AbstractExtension {

    @Override
    public Map<String, Filter> getFilters() {
        Map<String, Filter> filters = new HashMap<>();
        filters.put("base64", new Base64Filter());
        return filters;
    }

    private static class Base64Filter implements Filter {

        @Override
        public List<String> getArgumentNames() {
            return null;
        }

        @Override
        public Object apply(Object input, Map<String, Object> args) {
            if (input instanceof byte[]) {
                byte[] bytes = (byte[]) input;
                return BaseEncoding.base64().encode(bytes);
            }
            return input;
        }

    }

    @Override
    public Map<String, Function> getFunctions() {
        Map<String, Function> functions = new HashMap<>();
        functions.put("i18n", new I18NFunction());
        return functions;
    }

    @Override
    public Map<String, Test> getTests() {
        Map<String, Test> tests = new HashMap<>();
        tests.put("in", new InTest());
        return tests;
    }

    private static class I18NFunction implements Function {
        @Override
        public List<String> getArgumentNames() {
            List<String> names = new ArrayList<>();
            names.add("bundle");
            names.add("key");
            return names;
        }

        @Override
        public Object execute(Map<String, Object> args) {
            String bundle = (String) args.get("bundle");
            String key = args.get("key").toString();

            return BundleUtil.getString(bundle, key);
        }
    }

    private static class InTest implements Test {
        @Override
        public List<String> getArgumentNames() {
            return Collections.singletonList("collection");
        }

        @Override
        public boolean apply(Object input, Map<String, Object> args) {
            Collection<?> collection = (Collection<?>) args.get("collection");
            return collection.contains(input);
        }
    }

}
