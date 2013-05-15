package pt.ist.bennu.portal.domain;

import org.apache.commons.lang.StringUtils;

import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.dsi.commons.i18n.LocalizedString;
import pt.ist.fenixframework.Atomic;

public class HostInfo extends HostInfo_Base {

    private HostInfo() {
        super();
    }

    public HostInfo(VirtualHost virtualHost) {
        this();
        setHost(virtualHost);
    }

    @Atomic
    public void delete() {
        final VirtualHost host = getHost();
        host.setInfo(null);
        host.delete();
        if (host.getMenu() != null) {
            host.getMenu().delete();
        }
        deleteDomainObject();
    }

    private LocalizedString mls(LocalizedString mls) {
        return mls == null ? new LocalizedString() : mls;
    }

    @Override
    public void setApplicationCopyright(LocalizedString applicationCopyright) {
        super.setApplicationCopyright(mls(applicationCopyright));
    }

    @Override
    public void setApplicationSubTitle(LocalizedString applicationSubTitle) {
        super.setApplicationSubTitle(mls(applicationSubTitle));
    }

    @Override
    public void setHtmlTitle(LocalizedString htmlTitle) {
        super.setHtmlTitle(mls(htmlTitle));
    }

    public boolean hasLogo() {
        return getLogo() != null && getLogo().length > 0;
    }

    public boolean hasLogoType() {
        return !StringUtils.isEmpty(getLogoType());
    }
}
