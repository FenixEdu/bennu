package org.fenixedu.bennu.toolkit.components;

import org.jsoup.nodes.Element;

@ToolkitComponent(key = "vimeo", name = "Vimeo", description = "Embed a Vimeo Video", category = "media",
        editorFiles = { "/bennu-toolkit/js/components/vimeo.js" }, viewerFiles = { "/bennu-toolkit/img/vimeo.png" })
public class VimeoComponent extends Component {

    @Override
    public Element process(Element element) {
        throw new UnsupportedOperationException("This method is not implemented as it should never be used.");
    }

}
