package org.fenixedu.bennu.toolkit;

import java.util.Locale;
import java.util.function.Function;

import org.fenixedu.commons.i18n.LocalizedString;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class Sanitization {

    private static PolicyFactory TOOLKIT_SANITIZER = new HtmlPolicyBuilder()
            .allowStyling()
            .allowStandardUrlProtocols()
            .allowElements("a", "b", "blockquote", "br", "caption", "cite", "code", "col", "colgroup", "dd", "dl", "dt", "em",
                    "h1", "h2", "h3", "h4", "h5", "h6", "i", "img", "li", "ol", "p", "pre", "q", "small", "strike", "strong",
                    "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", "ul", "div", "font", "span")
            .allowAttributes("href")
            .onElements("a")
            .allowAttributes("title")
            .onElements("a")
            .allowAttributes("cite")
            .onElements("blockquote")
            .allowAttributes("span")
            .onElements("col")
            .allowAttributes("width")
            .onElements("col")
            .allowAttributes("span")
            .onElements("colgroup")
            .allowAttributes("width")
            .onElements("colgroup")
            .allowAttributes("align")
            .onElements("img")
            .allowAttributes("alt")
            .onElements("img")
            .allowAttributes("height")
            .onElements("img")
            .allowAttributes("src")
            .onElements("img")
            .allowAttributes("title")
            .onElements("img")
            .allowAttributes("width")
            .onElements("img")
            .allowAttributes("start")
            .onElements("ol")
            .allowAttributes("type")
            .onElements("ol")
            .allowAttributes("cite")
            .onElements("q")
            .allowAttributes("summary")
            .onElements("table")
            .allowAttributes("width")
            .onElements("table")
            .allowAttributes("abbr")
            .onElements("td")
            .allowAttributes("axis")
            .onElements("td")
            .allowAttributes("colspan")
            .onElements("td")
            .allowAttributes("rowspan")
            .onElements("td")
            .allowAttributes("width")
            .onElements("td")
            .allowAttributes("abbr")
            .onElements("th")
            .allowAttributes("axis")
            .onElements("th")
            .allowAttributes("colspan")
            .onElements("th")
            .allowAttributes("rowspan")
            .onElements("th")
            .allowAttributes("scope")
            .onElements("th")
            .allowAttributes("width")
            .onElements("th")
            .allowAttributes("type")
            .onElements("ul")
            .allowAttributes("class", "color")
            .globally()
            .allowAttributes("bennu-component", "component-resizable", "data-x", "data-y", "data-height", "data-width",
                    "data-source", "data-metadata")
            .onElements("div", "span", "a", "img", "h1", "h2", "h3", "h4", "h5", "h6", "p", "table").allowElements().toFactory();

    private static Function<String, String> sanitizer = (origin) -> TOOLKIT_SANITIZER.sanitize(origin);

    public static LocalizedString sanitize(LocalizedString origin) {
        LocalizedString result = new LocalizedString();

        for (Locale l : origin.getLocales()) {
            result = result.with(l, sanitize(origin.getContent(l)));
        }

        return result;
    }

    public static String sanitize(String original) {
        return sanitizer.apply(original);
    }

}
