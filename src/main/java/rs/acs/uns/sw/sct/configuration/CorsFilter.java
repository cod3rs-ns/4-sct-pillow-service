package rs.acs.uns.sw.sct.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CORS Filter settings.
 */
@Component
public class CorsFilter implements Filter {

    @Value("${sct.token.header}")
    private String tokenHeader;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Standard init - must override
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        final HttpServletResponse response = (HttpServletResponse) res;

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, X-SCT-Alert, X-SCT-Params, X-SCT-Error-Key, " + tokenHeader);
        response.setHeader("Access-Control-Expose-Headers", "Link, X-Total-Count, X-SCT-Alert, X-SCT-Params, X-SCT-Error-Key");
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        // Standard destroy - must override
    }
}
