package org.fenixedu.bennu.portal.servlet;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;

/**
 * This handler extends the default {@link PortalExceptionHandler}, by showing
 * a page with more relevant information, when working in development mode.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
public class PortalDevModeExceptionHandler extends PortalExceptionHandler {

    public PortalDevModeExceptionHandler(final ServletContext context) {
        super(new ClasspathLoader() {
            @Override
            public Reader getReader(String themeName) throws LoaderException {
                return new InputStreamReader(context.getResourceAsStream("/bennu-portal/debugExceptionPage.html"),
                        StandardCharsets.UTF_8);
            }
        }, context);
    }

    @Override
    protected void setExtraParameters(Map<String, Object> ctx, HttpServletRequest req, Throwable exception) {
        ctx.put("threadName", Thread.currentThread().getName());
        ctx.put("session", getSessionAttributes(req.getSession(false)));
        ctx.put("throwableInfos", ThrowableInfo.getFlatThrowableInfoList(exception));
    }

    private Object getSessionAttributes(HttpSession session) {
        if (session != null) {
            Map<String, Object> attrs = new TreeMap<>();
            Enumeration<String> names = session.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                attrs.put(name, session.getAttribute(name));
            }
            return attrs.entrySet();
        }
        return Collections.emptyList();
    }

    static class ThrowableInfo {
        private final boolean cause;
        private final boolean suppressed;
        private final int level;
        private final Throwable subject;
        private final List<ElementInfo> subjectInfo;

        public ThrowableInfo(boolean isCause, boolean isSurpressed, int level, Throwable subject) {
            super();
            this.cause = isCause;
            this.suppressed = isSurpressed;
            this.level = level;
            this.subject = subject;
            this.subjectInfo = getSubjectInfo(subject);
        }

        public boolean isCause() {
            return cause;
        }

        public boolean isSuppressed() {
            return suppressed;
        }

        public int getLevel() {
            return level;
        }

        public Throwable getSubject() {
            return subject;
        }

        public List<ElementInfo> getSubjectInfo() {
            return subjectInfo;
        }

        public String getLocalizedMessage() {
            String message = subject.getLocalizedMessage();
            return message == null ? null : message.replace("\n", "<br />");
        }

        private static List<ElementInfo> getSubjectInfo(Throwable subject) {
            List<ElementInfo> subjectInfo = new ArrayList<>();
            for (StackTraceElement element : subject.getStackTrace()) {
                subjectInfo.add(new ElementInfo(element));
            }
            return subjectInfo;
        }

        public static List<ThrowableInfo> getFlatThrowableInfoList(Throwable t) {
            return getFlatThrowableInfoList(t, false, false, 0);
        }

        private static List<ThrowableInfo> getFlatThrowableInfoList(Throwable t, boolean isCause, boolean isSurpressed, int level) {
            List<ThrowableInfo> list = new ArrayList<ThrowableInfo>();
            list.add(new ThrowableInfo(isCause, isSurpressed, level, t));
            for (Throwable supp : t.getSuppressed()) {
                //suppressed are presented one level below
                list.addAll(getFlatThrowableInfoList(supp, false, true, level + 1));
            }
            if (t.getCause() != null) {
                //cause is presented at the same level
                list.addAll(getFlatThrowableInfoList(t.getCause(), true, false, level));
            }
            return list;
        }
    }

    static class ElementInfo {
        private final StackTraceElement element;
        private final boolean isExternalClass;
        private final String simpleClassName;
        private final String packageName;

        public ElementInfo(StackTraceElement element) {
            this.element = element;
            String className = element.getClassName();
            this.simpleClassName = getSimpleClassName(className);
            this.packageName = className.substring(0, className.lastIndexOf("."));
            this.isExternalClass = !(className.startsWith("pt.ist") || className.contains(".fenixedu."));
        }

        private String getSimpleClassName(String className) {
            String[] parse = className.split("\\.");
            return parse[parse.length - 1];
        }

        public StackTraceElement getElement() {
            return element;
        }

        public boolean isExternalClass() {
            return isExternalClass;
        }

        public String getSimpleClassName() {
            return simpleClassName;
        }

        public String getPackageName() {
            return packageName;
        }

    }

}
