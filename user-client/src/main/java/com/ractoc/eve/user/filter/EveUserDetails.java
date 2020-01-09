package com.ractoc.eve.user.filter;


import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class EveUserDetails extends User {

    private int charId;
    private long expiresAt;
    private String ipAddress;

    public EveUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
                          int charId,
                          long expiresAt,
                          String ipAddress) {
        super(username, password, authorities);
        this.charId = charId;
        this.expiresAt = expiresAt;
        this.ipAddress = ipAddress;
    }
}
