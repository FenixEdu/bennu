package pt.ist.emailNotifier.domain;

import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.fenixWebFramework.services.Service;

public class EmailNotifier extends EmailNotifier_Base {

	private static EmailNotifier instance = null;

	@Service
	public synchronized static void initialize() {
		final MyOrg myOrg = MyOrg.getInstance();
		instance = myOrg.getEmailNotifier();
		if (instance == null) {
			instance = new EmailNotifier();
			myOrg.setEmailNotifier(instance);
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
