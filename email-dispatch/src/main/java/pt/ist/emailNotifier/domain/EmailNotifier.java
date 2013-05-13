package pt.ist.emailNotifier.domain;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.service.Service;

public class EmailNotifier extends EmailNotifier_Base {

    private static EmailNotifier instance = null;

    @Service
    public synchronized static void initialize() {
        final Bennu bennu = Bennu.getInstance();
        instance = bennu.getEmailNotifier();
        if (instance == null) {
            instance = new EmailNotifier();
            bennu.setEmailNotifier(instance);
        }
    }

    public static EmailNotifier getInstance() {
        if (instance == null) {
            initialize();
        }
        return instance;
    }

    private EmailNotifier() {
        super();
    }

}
