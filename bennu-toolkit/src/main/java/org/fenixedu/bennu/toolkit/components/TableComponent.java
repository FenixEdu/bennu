package org.fenixedu.bennu.toolkit.components;

import org.jsoup.nodes.Element;

@ToolkitComponent(key = "table", name = "Table", description = "Adds a table",
        editorFiles = { "/bennu-toolkit/js/components/table.js" })
public class TableComponent extends Component {

    @Override
    public Element process(Element element) {
        return element;
    }

}
