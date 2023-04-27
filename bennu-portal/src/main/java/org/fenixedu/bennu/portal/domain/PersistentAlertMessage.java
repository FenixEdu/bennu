package org.fenixedu.bennu.portal.domain;

import org.fenixedu.bennu.alerts.AlertType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

public class PersistentAlertMessage extends PersistentAlertMessage_Base {
    
    public PersistentAlertMessage(final AlertType type, final LocalizedString message,
                                  final DateTime hideAfterDateTime, final Integer hideAfterViewCount) {
        setBennu(Bennu.getInstance());
        setType(type);
        setMessage(message);
        setHideAfterDateTime(hideAfterDateTime);
        setHideAfterViewCount(hideAfterViewCount);
    }

    public void delete() {
        getPersistentAlertMessageUserViewCountSet().stream().forEach(PersistentAlertMessageUserViewCount::delete);
        setBennu(null);
        deleteDomainObject();
    }

}
