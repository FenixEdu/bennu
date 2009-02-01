package myorg.domain;

import java.io.Serializable;

import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class VirtualHostBean implements Serializable {

    private String hostname;
    private MultiLanguageString applicationTitle;
    private MultiLanguageString applicationSubTitle;
    private MultiLanguageString applicationCopyright;

    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    public MultiLanguageString getApplicationTitle() {
        return applicationTitle;
    }
    public void setApplicationTitle(MultiLanguageString applicationTitle) {
        this.applicationTitle = applicationTitle;
    }
    public MultiLanguageString getApplicationSubTitle() {
        return applicationSubTitle;
    }
    public void setApplicationSubTitle(MultiLanguageString applicationSubTitle) {
        this.applicationSubTitle = applicationSubTitle;
    }
    public MultiLanguageString getApplicationCopyright() {
        return applicationCopyright;
    }
    public void setApplicationCopyright(MultiLanguageString applicationCopyright) {
        this.applicationCopyright = applicationCopyright;
    }

}
