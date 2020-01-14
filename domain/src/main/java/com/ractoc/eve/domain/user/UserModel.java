package com.ractoc.eve.domain.user;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@ApiModel(value = "User Model", description = "Contains the user model information")
public class UserModel {

    private int charId;
    private String characterName;
    private String eveState;
    private long expiresAt;
    private String ipAddress;
    private String accessToken;

}
