package com.ractoc.eve.user.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class EveUserRegistration {

    int characterId;
    String name;
    String ipAddress;
    String eveState;
    String refreshToken;
    LocalDateTime lastrefresh;
    int expiresIn;

}
