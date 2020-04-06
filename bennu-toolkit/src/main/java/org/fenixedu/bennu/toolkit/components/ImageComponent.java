package org.fenixedu.bennu.toolkit.components;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

@ToolkitComponent(key = "image", name = "Image", description = "Add external image",
        editorFiles = { "/bennu-toolkit/js/components/images.js" })
public class ImageComponent extends Component {

    @Override
    public Element process(Element element) {

        Element image = new Element(Tag.valueOf("img"), "");

        String width = "";

        if (element.hasAttr("data-width") && !element.attr("data-width").equals("")) {
            width = element.attr("data-width");
        }

        String height = "";

        if (element.hasAttr("data-height") && !element.attr("data-height").equals("")) {
            height = element.attr("data-height");
        }

        image.attr("width", width);
        image.attr("height", height);
        image.attr("src", element.attr("data-source"));

        return image;
    }

}
