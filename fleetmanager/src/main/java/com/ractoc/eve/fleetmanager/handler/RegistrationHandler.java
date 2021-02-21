package com.ractoc.eve.fleetmanager.handler;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.domain.fleetmanager.RegistrationModel;
import com.ractoc.eve.fleetmanager.mapper.FleetMapper;
import com.ractoc.eve.fleetmanager.mapper.RegistrationMapper;
import com.ractoc.eve.fleetmanager.service.FleetService;
import com.ractoc.eve.fleetmanager.service.NoSuchEntryException;
import com.ractoc.eve.fleetmanager.service.RegistrationService;
import com.ractoc.eve.fleetmanager.validator.FleetValidator;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.CharacterApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@Slf4j
public class RegistrationHandler {

    private final FleetService fleetService;
    private final RegistrationService registrationService;
    private final FleetValidator fleetValidator;
    private final CharacterApi characterApi;

    public RegistrationHandler(RegistrationService registrationService,
                               FleetService fleetService,
                               FleetValidator fleetValidator,
                               CharacterApi characterApi) {
        this.registrationService = registrationService;
        this.fleetValidator = fleetValidator;
        this.characterApi = characterApi;
        this.fleetService = fleetService;
    }

    public List<RegistrationModel> getRegistrationsForFleet(Integer fleetId, int charId) {
        try {
            FleetModel fleet = FleetMapper.INSTANCE.dbToModel(fleetService.getFleet(fleetId).orElseThrow(() -> new NoSuchEntryException("fleet not found")));
            Integer corpId = characterApi.getCharactersCharacterId(charId, null, null).getCorporationId();
            if (fleetValidator.verifyFleet(fleet, charId, corpId)) {
                return registrationService.getRegistrationsForFleet(fleetId).map(RegistrationMapper.INSTANCE::dbToModel).collect(Collectors.toList());
            } else {
                throw new SecurityException("Access Denied");
            }
        } catch (ApiException e) {
            throw new HandlerException("Unable to fetch data from EVE ESI", e);
        }
    }

    public RegistrationModel getRegistrationsForFleetForCharacter(Integer fleetId, Integer characterId, int charId) {
        try {
            FleetModel fleet = FleetMapper.INSTANCE.dbToModel(fleetService.getFleet(fleetId).orElseThrow(() -> new NoSuchEntryException("fleet not found")));
            Integer corpId = characterApi.getCharactersCharacterId(charId, null, null).getCorporationId();
            if (fleetValidator.verifyFleet(fleet, charId, corpId)) {
                return RegistrationMapper.INSTANCE.dbToModel(registrationService.getRegistrationsForFleetForCharacter(fleetId, characterId));
            } else {
                throw new SecurityException("Access Denied");
            }
        } catch (ApiException e) {
            throw new HandlerException("Unable to fetch data from EVE ESI", e);
        }
    }

    public List<RegistrationModel> registerForFleet(Integer fleetId, int charId) {
        try {
            FleetModel fleet = FleetMapper.INSTANCE.dbToModel(fleetService.getFleet(fleetId).orElseThrow(() -> new NoSuchEntryException("fleet not found")));
            if (fleet.isRestricted()) {
                throw new HandlerException("Not allowed to register directly on a restricted fleet.");
            }
            Integer corpId = characterApi.getCharactersCharacterId(charId, null, null).getCorporationId();
            if (fleetValidator.verifyFleet(fleet, charId, corpId)) {
                String charName = characterApi.getCharactersCharacterId(charId,
                        null,
                        null).getName();
                String ownerName = characterApi.getCharactersCharacterId(fleet.getOwner(),
                        null,
                        null).getName();
                registrationService.registerForFleet(
                        fleetId,
                        charId,
                        charName);
                registrationService.sendRegistrationNotification(fleetId, fleet.getName(), charId, charName, fleet.getOwner(), ownerName);
                return getRegistrationsForFleet(fleetId, charId);
            } else {
                throw new SecurityException("Access Denied");
            }
        } catch (ApiException e) {
            throw new HandlerException("Unable to fetch data from EVE ESI", e);
        }
    }

    public void updateRegistration(RegistrationModel registration, Integer charId) {
        try {
            FleetModel fleet = FleetMapper.INSTANCE.dbToModel(fleetService.getFleet(registration.getFleetId())
                    .orElseThrow(() -> new NoSuchEntryException("fleet not found")));
            Integer corpId = characterApi.getCharactersCharacterId(charId, null, null).getCorporationId();
            if (fleetValidator.verifyFleet(fleet, charId, corpId)) {
                registrationService.updateRegistration(RegistrationMapper.INSTANCE.modelToDB(registration));
            } else {
                deleteRegistration(registration.getFleetId(), charId);
                throw new SecurityException("Access Denied");
            }
        } catch (ApiException e) {
            throw new HandlerException("Unable to fetch data from EVE ESI", e);
        }
    }

    public List<RegistrationModel> deleteRegistration(int fleetId, int charId) {
        try {
            FleetModel fleet = FleetMapper.INSTANCE.dbToModel(fleetService.getFleet(fleetId).orElseThrow(() -> new NoSuchEntryException("fleet not found")));
            Integer corpId = characterApi.getCharactersCharacterId(charId, null, null).getCorporationId();
            if (fleetValidator.verifyFleet(fleet, charId, corpId)) {
                registrationService.deleteRegistration(fleetId, charId);
                return registrationService.getRegistrationsForFleet(fleetId).map(RegistrationMapper.INSTANCE::dbToModel).collect(Collectors.toList());
            } else {
                throw new SecurityException("Access Denied");
            }
        } catch (ApiException e) {
            throw new HandlerException("Unable to fetch data from EVE ESI", e);
        }
    }
}
