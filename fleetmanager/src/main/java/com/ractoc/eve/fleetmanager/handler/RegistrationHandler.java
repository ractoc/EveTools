package com.ractoc.eve.fleetmanager.handler;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.domain.fleetmanager.RegistrationModel;
import com.ractoc.eve.fleetmanager.mapper.RegistrationMapper;
import com.ractoc.eve.fleetmanager.model.RegistrationConfirmation;
import com.ractoc.eve.fleetmanager.service.InviteService;
import com.ractoc.eve.fleetmanager.service.RegistrationService;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.CharacterApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Slf4j
public class RegistrationHandler {

    private final FleetHandler fleetHandler;
    private final RegistrationService registrationService;
    private final InviteService inviteService;
    private final CharacterApi characterApi;

    public RegistrationHandler(RegistrationService registrationService,
                               InviteService inviteService,
                               FleetHandler fleetHandler,
                               CharacterApi characterApi) {
        this.registrationService = registrationService;
        this.inviteService = inviteService;
        this.fleetHandler = fleetHandler;
        this.characterApi = characterApi;
    }

    public RegistrationModel registerForFleet(Integer fleetId, RegistrationConfirmation confirmation, int charId, String accessToken) {
        try {
            FleetModel fleet = fleetHandler.getFleet(fleetId, charId);
            String charName = characterApi.getCharactersCharacterId(charId,
                    null,
                    null).getName();
            String ownerName = characterApi.getCharactersCharacterId(fleet.getOwner(),
                    null,
                    null).getName();
            if (confirmation.isAccept()) {
                RegistrationModel registration = RegistrationMapper.INSTANCE.dbToModel(
                        registrationService.registerForFleet(
                                fleetId,
                                charId,
                                charName));
                inviteService.deleteInvitation(fleetId, charId);
                registrationService.sendRegistrationNotification(fleet.getName(), charId, charName, fleet.getOwner(), ownerName, accessToken);
                return registration;
            } else {
                inviteService.deleteInvitation(fleetId, charId);
                registrationService.sendDenyNotification(fleet.getName(), charId, charName, fleet.getOwner(), ownerName, accessToken);
                return null;
            }
        } catch (ApiException e) {
            throw new HandlerException("Unable to fetch data from EVE ESI", e);
        }
    }
}
