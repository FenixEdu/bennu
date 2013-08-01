package pt.ist.bennu.legacy.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import pt.ist.dsi.commons.i18n.I18N;
import pt.utl.ist.fenix.tools.util.i18n.Language;

@WebFilter("*.do")
public class LanguageFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        try {
            Language.setLocale(I18N.getLocale());
            chain.doFilter(request, response);
        } finally {
            Language.setLocale(null);
        }
    }

    @Override
    public void destroy() {

    }

}
