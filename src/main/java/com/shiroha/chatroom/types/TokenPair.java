package com.shiroha.chatroom.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * jwt响应封装类
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenPair {

    @JsonProperty(value = "access_token", required = true)
    private String accessToken;

    @JsonProperty(value = "expires_in", required = true)
    private Instant expiresIn;

    @JsonProperty(value = "refresh_token", required = true)
    private String refreshToken;

    @JsonProperty(value = "refresh_expires_in", required = true)
    private Instant refreshExpiresIn;

    @JsonProperty(value = "token_type", defaultValue = "Bearer")
    private String tokenType;

    @JsonProperty(value = "session_state")
    private String sessionState;

    @JsonProperty(value = "scope")
    private String scope;
}
