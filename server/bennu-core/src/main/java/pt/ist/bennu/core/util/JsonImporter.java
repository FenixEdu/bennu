package pt.ist.bennu.core.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import pt.ist.dsi.commons.i18n.I18N;
import pt.ist.dsi.commons.i18n.LocalizedString;
import pt.ist.dsi.commons.i18n.LocalizedString.Builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class JsonImporter {
    private static JsonParser parser = new JsonParser();

    public static JsonElement importJson(String json) {
        try {
            return parser.parse(json);
        } catch (JsonSyntaxException jse) { // let's try to parse a MultiLanguageString
            Builder builder = new LocalizedString.Builder();
            Map<String, String> importFromString = importFromString(json);
            for (Entry<String, String> entry : importFromString.entrySet()) {
                builder.with(Locale.forLanguageTag(entry.getKey()), entry.getValue());
            }
            return builder.build().json();
        }

    }

    private static Map<String, String> importFromString(String string) {
        Map<String, String> contents = new HashMap<String, String>();
        String nullContent = StringUtils.EMPTY;

        for (int i = 0; i < string.length();) {

            int length = 0;
            int collonPosition = string.indexOf(':', i + 2);

            if (!StringUtils.isNumeric(string.substring(i + 2, collonPosition))) {
                length = Integer.parseInt(string.substring(i + 4, collonPosition));
                nullContent = string.substring(collonPosition + 1, collonPosition + 1 + length);
            } else {
                length = Integer.parseInt(string.substring(i + 2, collonPosition));
                String language = string.substring(i, i + 2);
                String content = string.substring(collonPosition + 1, collonPosition + 1 + length);
                contents.put(language, content);
            }

            i = collonPosition + 1 + length;
        }

        if (contents.isEmpty()) {
            contents.put(I18N.getLocale().getCountry(), nullContent);
        }

        return contents;
    }
}
