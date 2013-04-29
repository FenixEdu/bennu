package pt.ist.bennu.portal.domain;

import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.i18n.InternationalString;
import pt.ist.bennu.service.Service;

public class HostInfo extends HostInfo_Base {

    private HostInfo() {
        super();
    }

    public HostInfo(VirtualHost virtualHost) {
        this();
        setHost(virtualHost);
    }

    @Service
    public void delete() {
        final VirtualHost host = getHost();
        host.removeInfo();
        host.delete();
        if (host.hasMenu()) {
            host.getMenu().delete();
        }
        deleteDomainObject();
    }

    private InternationalString mls(InternationalString mls) {
        return mls == null ? new InternationalString() : mls;
    }

    @Override
    public void setApplicationCopyright(InternationalString applicationCopyright) {
        super.setApplicationCopyright(mls(applicationCopyright));
    }

    @Override
    public void setApplicationSubTitle(InternationalString applicationSubTitle) {
        super.setApplicationSubTitle(mls(applicationSubTitle));
    }

    @Override
    public void setHtmlTitle(InternationalString htmlTitle) {
        super.setHtmlTitle(mls(htmlTitle));
    }

}
