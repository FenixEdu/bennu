package myorg.domain.exceptions;


/* 
 * Copyright 2010 IT Mill Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

/**
 * <code>SystemError</code> is a runtime exception caused by error in system.
 * The system error can be shown to the user as it implements
 * <code>ErrorMessage</code> interface, but contains technical information such
 * as stack trace and exception.
 * 
 * @author IT Mill Ltd.
 * @version
 * 6.6.0
 * @since 3.0
 */
@SuppressWarnings("serial")
public class DomainExceptionErrorMessage extends RuntimeException implements ErrorMessage {

    /**
     * The cause of the system error. The cause is stored separately as JDK 1.3
     * does not support causes natively.
     */
    private Throwable cause = null;

    /**
     * Constructor for SystemError with error message specified.
     * 
     * @param message
     *            the Textual error description.
     */
    public DomainExceptionErrorMessage(String message) {
        super(message);
    }

    /**
     * Constructor for SystemError with causing exception and error message.
     * 
     * @param message
     *            the Textual error description.
     * @param cause
     *            the throwable causing the system error.
     */
    public DomainExceptionErrorMessage(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    /**
     * Constructor for SystemError with cause.
     * 
     * @param cause
     *            the throwable causing the system error.
     */
    public DomainExceptionErrorMessage(Throwable cause) {
        this.cause = cause;
    }

    /**
     * @see com.vaadin.terminal.ErrorMessage#getErrorLevel()
     */
    public final int getErrorLevel() {
        return ErrorMessage.ERROR;
    }

    /**
     * @see com.vaadin.terminal.Paintable#paint(com.vaadin.terminal.PaintTarget)
     */
    public void paint(PaintTarget target) throws PaintException {

        target.startTag("error");
        target.addAttribute("level", "error");

        StringBuilder sb = new StringBuilder();
        final String message = getLocalizedMessage();
        if (message != null) {
            sb.append("<h2>");
            sb.append(message);
            sb.append("</h2>");
        }

        target.addXMLSection("div", sb.toString(),
                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");

        target.endTag("error");

    }

    /**
     * Gets cause for the error.
     * 
     * @return the cause.
     * @see java.lang.Throwable#getCause()
     */
    @Override
    public Throwable getCause() {
        return cause;
    }

    /* Documented in super interface */
    public void addListener(RepaintRequestListener listener) {
    }

    /* Documented in super interface */
    public void removeListener(RepaintRequestListener listener) {
    }

    /* Documented in super interface */
    public void requestRepaint() {
    }

    /* Documented in super interface */
    public void requestRepaintRequests() {
    }

    public String getDebugId() {
        return null;
    }

    public void setDebugId(String id) {
        throw new UnsupportedOperationException(
                "Setting testing id for this Paintable is not implemented");
    }

}
