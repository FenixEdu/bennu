package org.fenixedu.bennu.portal.servlet;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * 
 * Wraps an {@link HttpServletResponse} so that writes to its PrintWriter
 * are buffered, so that the written value can later be rewritten, by the layout injector.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
class PortalResponseWrapper extends HttpServletResponseWrapper {
    private CharArrayWriter writer = null;

    public PortalResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new CharArrayWriter();
            getResponse().getWriter();
        }
        return new PrintWriter(writer);
    }

    public String getContent() {
        return writer.toString();
    }

    @Override
    public void resetBuffer() {
        super.resetBuffer();
        if (writer != null) {
            writer.reset();
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        /* Don't flush an empty buffer (this caused issues with static resources,
         * as getWriter() was called but nothing was written).
         */
        if (writer != null && writer.size() > 0) {
            getResponse().getWriter().write(writer.toString());
            writer.reset();
        }
        super.flushBuffer();
    }

    /*
     * The response only has data if getWriter() was called
     */
    public boolean hasData() {
        return writer != null;
    }

}