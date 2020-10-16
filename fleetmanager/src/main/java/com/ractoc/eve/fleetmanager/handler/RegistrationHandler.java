package com.ractoc.eve.fleetmanager.handler;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.domain.fleetmanager.RegistrationModel;
import com.ractoc.eve.fleetmanager.mapper.FleetMapper;
import com.ractoc.eve.fleetmanager.mapper.RegistrationMapper;
import com.ractoc.eve.fleetmanager.model.RegistrationConfirmation;
import com.ractoc.eve.fleetmanager.service.FleetService;
import com.ractoc.eve.fleetmanager.service.InviteService;
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
    private final InviteService inviteService;
    private final FleetValidator fleetValidator;
    private final CharacterApi characterApi;

    public RegistrationHandler(RegistrationService registrationService,
                               InviteService inviteService,
                               FleetService fleetService,
                               FleetValidator fleetValidator,
                               CharacterApi characterApi) {
        this.registrationService = registrationService;
        this.inviteService = inviteService;
        this.fleetValidator = fleetValidator;
        this.characterApi = characterApi;
        this.fleetService = fleetService;
    }

    public List<RegistrationModel> getRegistrationsForFleet(Integer fleetId, int charId) {
        FleetModel fleet = FleetMapper.INSTANCE.dbToModel(fleetService.getFleet(fleetId).orElseThrow(() -> new NoSuchEntryException("fleet not found")));
        if (fleetValidator.verifyFleet(fleet, charId)) {
            return registrationService.getRegistrationsForFleet(fleetId).map(RegistrationMapper.INSTANCE::dbToModel).collect(Collectors.toList());
        } else {
            throw new SecurityException("Access Denied");
        }
    }

    public RegistrationModel registerForFleet(Integer fleetId, RegistrationConfirmation confirmation, int charId, String accessToken) {
        try {
            FleetModel fleet = FleetMapper.INSTANCE.dbToModel(fleetService.getFleet(fleetId).orElseThrow(() -> new NoSuchEntryException("fleet not found")));
            if (fleetValidator.verifyFleet(fleet, charId)) {
                String charName = characterApi.getCharactersCharacterId(charId,
                        null,
                        null).getName();
                String ownerName = characterApi.getCharactersCharacterId(fleet.getOwner(),
                        null,
                        null).getName();
                RegistrationModel registration = RegistrationMapper.INSTANCE.dbToModel(
                        registrationService.registerForFleet(
                                fleetId,
                                charId,
                                charName,
                                confirmation.isAccept()));
                inviteService.deleteInvitation(fleetId, charId);
                if (confirmation.isAccept()) {
                    registrationService.sendRegistrationNotification(fleet.getName(), charId, charName, fleet.getOwner(), ownerName, accessToken);
                    return registration;
                } else {
                    registrationService.sendDenyNotification(fleet.getName(), charId, charName, fleet.getOwner(), ownerName, accessToken);
                    return null;
                }
            } else {
                throw new SecurityException("Access Denied");
            }
        } catch (ApiException e) {
            throw new HandlerException("Unable to fetch data from EVE ESI", e);
        }
    }

    public void deleteRegistration(int fleetId, int charId) {
        registrationService.deleteRegistration(fleetId, charId);
    }
}
