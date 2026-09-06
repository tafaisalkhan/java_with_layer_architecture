package com.mycloud.apigateway.authorization;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class DynamicAuthorizationFilter implements GlobalFilter, Ordered {
    private final WebClient authorizationClient;
    public DynamicAuthorizationFilter(@Value("${gateway.authorization-service-url:http://localhost:8091}") String url){authorizationClient=WebClient.builder().baseUrl(url).build();}

    @Override public Mono<Void> filter(ServerWebExchange exchange,GatewayFilterChain chain){
        String path=exchange.getRequest().getPath().value(); HttpMethod method=exchange.getRequest().getMethod();
        if(isPublic(method,path))return chain.filter(exchange);
        return exchange.getPrincipal().ofType(JwtAuthenticationToken.class).flatMap(authentication->{
            UUID actAs=parseActAs(exchange.getRequest().getHeaders().getFirst("X-Act-As-Customer-ID"));
            CheckRequest request=new CheckRequest(authentication.getToken().getSubject(),method.name(),path,actAs);
            return authorizationClient.post().uri("/authorization/check").bodyValue(request).retrieve().bodyToMono(Decision.class)
                .flatMap(decision->{if(!decision.allowed())return reject(exchange,HttpStatus.FORBIDDEN);var mutated=exchange.getRequest().mutate().headers(headers->{headers.remove("X-Actor-ID");headers.remove("X-Effective-Account-Type");headers.remove("X-Effective-Tenant-ID");headers.remove("X-Impersonating");headers.set("X-Actor-ID",String.valueOf(decision.actorId()));headers.set("X-Effective-Account-Type",decision.effectiveAccountType());if(decision.effectiveTenantId()!=null)headers.set("X-Effective-Tenant-ID",decision.effectiveTenantId().toString());headers.set("X-Impersonating",Boolean.toString(decision.impersonating()));}).build();return chain.filter(exchange.mutate().request(mutated).build());})
                .onErrorResume(error->reject(exchange,HttpStatus.SERVICE_UNAVAILABLE));
        }).switchIfEmpty(reject(exchange,HttpStatus.UNAUTHORIZED));
    }
    private UUID parseActAs(String value){if(value==null||value.isBlank())return null;try{return UUID.fromString(value);}catch(IllegalArgumentException e){throw new IllegalArgumentException("invalid X-Act-As-Customer-ID",e);}}
    private boolean isPublic(HttpMethod method,String path){return path.equals("/actuator/health")||path.equals("/actuator/info")||method==HttpMethod.OPTIONS||(method==HttpMethod.POST&&path.equals("/api/users"))||(method==HttpMethod.GET&&(path.equals("/api/products")||path.startsWith("/api/products/")));}
    private Mono<Void> reject(ServerWebExchange exchange,HttpStatus status){exchange.getResponse().setStatusCode(status);return exchange.getResponse().setComplete();}
    @Override public int getOrder(){return 0;}
    private record CheckRequest(String subject,String method,String path,UUID actAsCustomerId){}
    private record Decision(boolean allowed,String reason,UUID actorId,String effectiveAccountType,UUID effectiveTenantId,boolean impersonating){}
}
