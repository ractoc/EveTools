package com.ractoc.eve.user.filter;


import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@ToString
public class EveUserDetails extends User {

    private int charId;
    private long expiresAt;
    private String accessToken;

    public EveUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
                          int charId,
                          long expiresAt,
                          String accessToken) {
        super(username, password, authorities);
        this.charId = charId;
        this.expiresAt = expiresAt;
        this.accessToken = accessToken;
    }
}
