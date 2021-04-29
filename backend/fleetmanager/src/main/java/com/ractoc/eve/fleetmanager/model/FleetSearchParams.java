package com.ractoc.eve.fleetmanager.model;

import com.ractoc.eve.domain.fleetmanager.TypeModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FleetSearchParams {
    private LocalDateTime start;
    private LocalDateTime end;
    private TypeModel[] fleetTypes;
    private boolean invited;
    private boolean registered;
    private boolean owned;
}
