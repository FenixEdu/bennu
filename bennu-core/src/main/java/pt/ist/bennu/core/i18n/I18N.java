package pt.ist.bennu.core.i18n;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class I18N {
    private static final Logger logger = LoggerFactory.getLogger(I18N.class);

    private static final String LOCALE_KEY = I18N.class.getName() + "_LOCAL_KEY";

    private static final InheritableThreadLocal<Locale> locale = new InheritableThreadLocal<>();

    /**
     * Gets the {@link Locale} for this thread. The locale returned is the first hit in the following chain:
     * <ul>
     * <li>A manual, session scoped, setting</li>
     * <li>The user preferred language</li>
     * <li>The java-vm default locale</li>
     * </ul>
     * 
     * @return The {@link Locale} instance, never null
     */
    public static Locale getLocale() {
        return locale.get() != null ? locale.get() : Locale.getDefault();
    }

    /**
     * Sets the {@link Locale} for current thread, and for current session (if a session is passed)
     * 
     * @param session Option session instance,
     * @param locale Locale to set
     */
    public static void setLocale(HttpSession session, Locale locale) {
        if (session != null) {
            session.setAttribute(LOCALE_KEY, locale);
        }
        I18N.locale.set(locale);
        logger.debug("Set locale to: {}", locale.toString());
    }

    static void updateFromSession(HttpSession session) {
        if (session != null && session.getAttribute(LOCALE_KEY) != null) {
            locale.set((Locale) session.getAttribute(LOCALE_KEY));
            logger.debug("Set thread's locale to: {}", locale.get().toString());
        } else {
            locale.set(null);
        }
    }

    static void clear() {
        locale.set(null);
    }
}
