package org.fenixedu.bennu.toolkit.components;

import org.jsoup.nodes.Element;

@ToolkitComponent(key = "generic", name = "Generic source", description = "Embed a video using HTML source (share/embedded)",
        category = "media", editorFiles = { "/bennu-toolkit/js/components/generic.js" })
public class GenericMediaComponent extends Component {

    @Override
    public Element process(Element element) {
        throw new UnsupportedOperationException("This method is not implemented as it should never be used.");
    }

}
