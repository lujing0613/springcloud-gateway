package com.teemor.filter;

import com.teemor.plugin.activemq.Producer;
import com.teemor.plugin.redis.service.RedisService;
import com.teemor.utils.WebUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

/**
 * 日志过滤器
 *
 * @author lujing
 * @date 2019-05-28 16:49
 */
@Component
@Order(2000)
public class LogFilter implements GlobalFilter {
    
    private HashSet<String> logURI = new HashSet<>();
    
    
    @Autowired
    private RedisService redisService;
    
    
    @Autowired
    private Producer producer;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        request.getHeaders();
        ServerHttpResponse response = exchange.getResponse();
        
        request.getQueryParams();
        String requestUrl = WebUtil.getOriginUrl(exchange);
        if (!isLogAble(requestUrl)) {
            return chain.filter(exchange);
        }
        
        JSONObject logInfo = new JSONObject();
        JSONObject action = JSONObject.fromObject(redisService.getActionByUrl(requestUrl));
        
        
        logInfo.put("id", UUID.randomUUID().toString().replace("-", ""));
        String result = "";
        String reqBody = "";
        String ticket = WebUtil.getTicketByRequest(request);
        String reqParam = null;
//        try {
//            InputStream in = response.();
//            reqBody = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
//            JSONObject json = JSONObject.fromObject(reqBody);
//            result = json.getString("code");
//            if (url.indexOf("login") != -1) {
//                ticket = json.getString("data");
//            }
//            reqParam = JSONObject.fromObject(request.getQueryParams()).toString();
//        } catch (Exception e) {
//            result = "1005";
//        }
        
        logInfo.put("reqParam", "\"" + reqParam + "\"");
        JSONObject user = (JSONObject) redisService.getUserByToken(ticket);
        if (user != null) {
            logInfo.put("userId", user.get("userId"));
            logInfo.put("userName", user.get("userName"));
            logInfo.put("scenicId", user.get("scenicId"));
            logInfo.put("companyId", user.get("companyId"));
        }
        String ipAddress = Objects.requireNonNull(request.getRemoteAddress()).getAddress().toString();
        logInfo.put("ip", ipAddress.replace("/", ""));
//        if ("1001".equals(result)) {
//            logInfo.put("result", 1);
//        } else {
        logInfo.put("result", 1);
//        }
        logInfo.put("operationName", action.get("actionName"));
        String centerId = action.getString("sysFlag");
        logInfo.put("centerId", centerId);
        //数据库url存在重复，Redis以url为key存储，这里获取的actionMenuId可能并不准确
        logInfo.put("actionMenuId", action.get("menuId"));
        logInfo.put("time", new Date());
        logInfo.put("level", action.get("actionType"));
        producer.send("log.queue", logInfo);
        
        
        return chain.filter(exchange);
    }
    
    
    /**
     * 检查是否需要记录日志
     *
     * @param requestUrl
     * @return
     */
    private boolean isLogAble(String requestUrl) {
        
        String action = redisService.getActionByUrl(requestUrl);
        if (StringUtils.isBlank(action)) {
            return false;
        }
        
        for (String s : logURI) {
            if (requestUrl.contains(s)) {
                return true;
                
            }
        }
        return false;
    }
    
    
    /**
     * 初始需要打印日志的uri
     */
    @PostConstruct
    private void initWhiteUrlList() {
        //需要校验的
        logURI.add("user-center");
        logURI.add("ticket-booking");
        logURI.add("log-center");
        
    }
}
