package com.ractoc.eve.fleetmanager.validator;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.domain.fleetmanager.RegistrationModel;
import com.ractoc.eve.fleetmanager.mapper.RegistrationMapper;
import com.ractoc.eve.fleetmanager.service.RegistrationService;
import com.ractoc.eve.jesi.api.CharacterApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistrationValidator {

    private final RegistrationService registrationService;
    private final CharacterApi characterApi;

    @Autowired
    public RegistrationValidator(RegistrationService registrationService, CharacterApi characterApi) {
        this.registrationService = registrationService;
        this.characterApi = characterApi;
    }

    public boolean verifyFleetRegistrations(FleetModel fleet, Integer charId) {
        return getRegistrationsForFleet(fleet).stream().anyMatch(registration -> verifyRegistration(registration, charId));
    }

    public boolean verifyRegistration(RegistrationModel registration, Integer charId) {
        if (registration.getCharacterId() != 0) {
            if (!registration.getCharacterId().equals(charId)) {
                return false;
            }
        }
        return true;
    }

    public List<RegistrationModel> getRegistrationsForFleet(FleetModel fleet) {
        return registrationService.getRegistrationsForFleet(fleet).map(RegistrationMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }
}
