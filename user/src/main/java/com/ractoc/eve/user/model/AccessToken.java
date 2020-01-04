package com.ractoc.eve.user.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessToken {
    private String access_token;
    private int expires_in;
    private String token_type;
    private String refresh_token;
}
