package org.fenixedu.bennu.toolkit.components;

import org.jsoup.nodes.Element;

@ToolkitComponent(key = "link", name = "Link", description = "Add a link to some place",
        editorFiles = { "/bennu-toolkit/js/components/link.js" })
public class LinkComponent extends Component {

    @Override
    public Element process(Element element) {
        element.removeAttr("bennu-component");
        return element;
    }

}
