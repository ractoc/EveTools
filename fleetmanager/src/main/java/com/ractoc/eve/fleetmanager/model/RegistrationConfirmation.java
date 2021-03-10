package com.ractoc.eve.fleetmanager.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@ApiModel(value = "Registration Confirmation Model", description = "contains the confirmation information for a fleet registration")
public class RegistrationConfirmation {

    private boolean accept;
}
