package com.ractoc.eve.fleetmanager.validator;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FleetValidator {

    private final InviteValidator inviteValidator;
    private final RegistrationValidator registrationValidator;

    @Autowired
    public FleetValidator(InviteValidator inviteValidator, RegistrationValidator registrationValidator) {
        this.inviteValidator = inviteValidator;
        this.registrationValidator = registrationValidator;
    }

    public boolean verifyFleet(FleetModel fleet, Integer charId) {
        return fleet.getOwner().equals(charId) ||
                !fleet.isRestricted() ||
                inviteValidator.verifyFleetInvites(fleet, charId) ||
                registrationValidator.verifyFleetRegistrations(fleet, charId);
    }
}
