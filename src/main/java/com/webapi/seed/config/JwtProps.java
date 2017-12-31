package com.webapi.seed.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProps {

    private String header;
    private String secret;
    private Long expiration;
    private String tokenHead;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public String getTokenHead() {
        return tokenHead;
    }

    public void setTokenHead(String tokenHead) {
        this.tokenHead = tokenHead;
    }

}
