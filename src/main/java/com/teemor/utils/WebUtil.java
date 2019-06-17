package com.teemor.utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.util.Set;

/**
 * web工具
 *
 * @author lujing
 * @date 2019-05-28 16:56
 */
public class WebUtil {
    
    
    /**
     * 获取登录凭证
     *
     * @param request
     * @return
     */
    public static String getTicketByRequest(ServerHttpRequest request) {
        
        String ticket = null;
        String TICKET_NAME = "ticket";
        HttpCookie cookie = request.getCookies().getFirst(TICKET_NAME);
        if (cookie != null) {
            ticket = cookie.getValue();
        }
        
        if (StringUtils.isBlank(ticket)) {
            ticket = request.getQueryParams().getFirst(TICKET_NAME);
        }
        if (StringUtils.isBlank(ticket)) {
            ticket = request.getHeaders().getFirst(TICKET_NAME);
        }
        return ticket;
    }
    
    
    /**
     * 获取路有前的真是请求路径
     *
     * @param exchange
     * @return
     */
    public static String getOriginUrl(ServerWebExchange exchange) {
        Set<URI> uris = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
        for (URI uri : uris) {
            if (uri.getScheme().contains("http")) {
                return uri.getPath().replaceAll("//", "/");
            }
        }
        return exchange.getRequest().getURI().getPath();
    }
}
