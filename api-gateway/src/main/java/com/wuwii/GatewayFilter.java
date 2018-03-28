package com.wuwii;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * zuul filter
 * @author Zhang Kai
 * @version 1.0
 * @since <pre>2018/3/28 17:53</pre>
 */
@Component
public class GatewayFilter extends ZuulFilter {
    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(GatewayFilter.class);

    /**
     * filter type:
     * "pre", pre-routing filtering,
     * "route" for routing to an origin,
     * "post" for post-routing filters,
     * "error" for error handling.
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * The precedence for a filter.
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * If return true then execute the filter.
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * To realize the filter.
     */
    @Override
    public Object run() throws ZuulException {
        log.info("Begin running the filter.");
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        log.info("URL: {} , Method: {}.", request.getRequestURL().toString(), request.getMethod());
        Object token = request.getParameter("token");
        if (token == null) {
            // false 不进行路由，并返回 401 状态码
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            ctx.setResponseBody("Miss the token.");
            return null;
        }
        log.info("The token is {}", token);
        return null;
    }
}
