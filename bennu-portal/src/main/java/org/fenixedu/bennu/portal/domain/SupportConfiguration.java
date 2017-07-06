package org.fenixedu.bennu.portal.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.Atomic;

public class SupportConfiguration extends SupportConfiguration_Base {

    public SupportConfiguration(LocalizedString title, String emailAddress) {
        super();
        setRoot(Bennu.getInstance());
        setTitle(title);
        setEmailAddress(emailAddress);
    }

    @Atomic
    public void delete() {
        setRoot(null);
        getMenuItemSet().stream().forEach(menuItem -> menuItem.setSupport(null));
        deleteDomainObject();
    }

}
