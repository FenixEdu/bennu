package org.fenixedu.bennu.toolkit.components;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

@ToolkitComponent(key = "youtube", name = "YouTube", description="Embebed a YouTube Video", editorFiles = { "/bennu-toolkit/js/components/youtube.js" })
public class YouTubeComponent extends Component {

    private String getYouTubeId(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);

        if (matcher.find()) {
            return matcher.group();
        }else{
            return "";
        }
        
    }

    @Override
    public Element process(Element element) {

        // <iframe width="560" height="315" src="https://www.youtube.com/embed/qORiU7UfJAM" frameborder="0" allowfullscreen></iframe>

        Element iframe = new Element(Tag.valueOf("iframe"), "");
        
        String width = "560";
        
        if (!element.attr("data-width").equals("")){
            width = element.attr("data-width"); 
        }

        String height = "315";
        
        if (!element.attr("data-height").equals("")){
            height = element.attr("data-height"); 
        }
        
        iframe.attr("width", width);
        iframe.attr("height", height);
        iframe.attr("src", "https://www.youtube.com/embed/" + getYouTubeId(element.attr("data-source")));
        iframe.attr("frameborder", "0");

        return iframe;
    }
}