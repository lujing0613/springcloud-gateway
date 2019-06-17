package com.teemor.filter;

import com.teemor.common.constant.ResponseConstant;
import com.teemor.plugin.redis.service.RedisService;
import com.teemor.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;

/**
 * 登录校验过滤器
 *
 * @author lujing
 * @date 2019-05-28 13:04
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter {
    
    private HashSet<String> whiteURL = new HashSet<>();
    
    private HashSet<String> blackURL = new HashSet<>();
    
    
    @Autowired
    private RedisService redisService;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String requestUrl = WebUtil.getOriginUrl(exchange);
        
        if (isWhiteList(request, requestUrl)) {
            return chain.filter(exchange);
        }
        log.info("请求路径：{}，请求方法：{}", requestUrl, request.getMethod());
        
        String ticket = WebUtil.getTicketByRequest(request);
        
        if (StringUtils.isBlank(ticket)) {
            
            return response.writeWith(Mono.just(
                    response.bufferFactory().wrap(ResponseConstant.NO_LOGIN.getBytes())));
        }
        if (redisService.getUserByToken(ticket) == null) {
            return response.writeWith(Mono.just(
                    response.bufferFactory().wrap(ResponseConstant.TICKET_TIME_OUT.getBytes())));
        }
        
        return chain.filter(exchange);
    }
    
    
    /**
     * 判断请求的方法是否需要鉴权
     *
     * @param request    请求体
     * @param requestUrl 真是请求路径
     * @return 是否是白名单（不需要校验）
     */
    private boolean isWhiteList(ServerHttpRequest request, String requestUrl) {
        
        
        //OPTIONS 不校验，直接通过
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return true;
        }
        //判断请求的路径是否包含在白名单
        for (String white : whiteURL) {
            if (requestUrl.contains(white)) {
                return true;
            }
        }
        //需要校验的名单
        for (String black : blackURL) {
            if (requestUrl.contains(black)) {
                return false;
            }
        }
        //其他的都不要校验
        return true;
        
    }
    
    
    /**
     * 初始化白名单
     */
    @PostConstruct
    private void initWhiteUrlList() {
        //白名单
        whiteURL.add("msg/ypCallBack");
        whiteURL.add("ogin");
        whiteURL.add("distribution");
        whiteURL.add("bindingAccessCode");
        whiteURL.add("heartbeat");
        //需要校验的
        blackURL.add("user-center");
        blackURL.add("ticket-booking");
        blackURL.add("log-center");
        
    }
    
    
  
    
    
}
