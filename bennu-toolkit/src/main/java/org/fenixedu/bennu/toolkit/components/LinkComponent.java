package org.fenixedu.bennu.toolkit.components;

import java.io.IOException;
import java.io.Writer;

import org.jsoup.nodes.Element;

import com.google.gson.JsonArray;

@ToolkitComponent(key = "link", name = "Link", description="Add a link to some place" ,editorFiles = { "/bennu-toolkit/js/components/link.js" })
public class LinkComponent extends Component {

    public Element process(Element element) {
        element.removeAttr("bennu-component");
        return element;
    }

}
