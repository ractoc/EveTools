package com.ractoc.eve.fleetmanager.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FleetSearchParams {
    private boolean owned;
    private boolean active;
    private Integer typeId;
}
