package com.ractoc.eve.fleetmanager.validator;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.domain.fleetmanager.RegistrationModel;
import com.ractoc.eve.fleetmanager.mapper.RegistrationMapper;
import com.ractoc.eve.fleetmanager.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationValidator {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationValidator(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    public boolean verifyFleetRegistrations(FleetModel fleet, Integer charId) {
        return registrationService.getRegistrationsForFleet(fleet.getId())
                .map(RegistrationMapper.INSTANCE::dbToModel)
                .anyMatch(registration -> verifyRegistration(registration, charId));
    }

    public boolean verifyRegistration(RegistrationModel registration, Integer charId) {
        if (registration.getCharacterId() != 0) {
            if (!registration.getCharacterId().equals(charId)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasRegistration(int fleetId, Integer charId) {
        return registrationService.getRegistration(fleetId, charId).isPresent();
    }
}
