package com.ractoc.eve.fleetmanager.validator;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FleetValidator {

    private final InviteValidator inviteValidator;

    @Autowired
    public FleetValidator(InviteValidator inviteValidator) {
        this.inviteValidator = inviteValidator;
    }

    public boolean verifyFleet(FleetModel fleet, Integer charId) {
        return !fleet.isCorporationRestricted() || inviteValidator.verifyFleetInvites(fleet, charId);
    }
}
