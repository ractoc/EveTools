package com.ractoc.eve.user.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthTokenRequest {
    private String grant_type;
    private String code;
}
