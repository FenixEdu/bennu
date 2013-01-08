package pt.ist.bennu.core.security;

import pt.ist.bennu.core.domain.User;

public class UserView {
	private static ThreadLocal<SessionUserWrapper> wrapper = new ThreadLocal<>();

	static SessionUserWrapper getSessionUserWrapper() {
		return wrapper.get();
	}

	static void setSessionUserWrapper(SessionUserWrapper newWrapper) {
		wrapper.set(newWrapper);
	}

	public static User getUser() {
		return wrapper.get() != null ? wrapper.get().getUser() : null;
	}

	public static boolean hasUser() {
		return wrapper.get() != null;
	}
}
