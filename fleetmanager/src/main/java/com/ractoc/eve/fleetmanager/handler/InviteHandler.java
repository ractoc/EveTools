package com.ractoc.eve.fleetmanager.handler;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.domain.fleetmanager.InviteModel;
import com.ractoc.eve.fleetmanager.mapper.FleetMapper;
import com.ractoc.eve.fleetmanager.mapper.InviteMapper;
import com.ractoc.eve.fleetmanager.service.FleetService;
import com.ractoc.eve.fleetmanager.service.InviteService;
import com.ractoc.eve.fleetmanager.service.NoSuchEntryException;
import com.ractoc.eve.fleetmanager.validator.FleetValidator;
import com.ractoc.eve.fleetmanager.validator.InviteValidator;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.CharacterApi;
import com.ractoc.eve.jesi.api.CorporationApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Slf4j
public class InviteHandler {

    private final InviteService inviteService;
    private final FleetService fleetService;
    private final InviteValidator inviteValidator;
    private final FleetValidator fleetValidator;
    private final CharacterApi characterApi;
    private final CorporationApi corporationApi;

    @Autowired
    public InviteHandler(InviteService inviteService,
                         FleetService fleetService,
                         InviteValidator inviteValidator,
                         FleetValidator fleetValidator,
                         CharacterApi characterApi,
                         CorporationApi corporationApi) {
        this.inviteService = inviteService;
        this.fleetService = fleetService;
        this.inviteValidator = inviteValidator;
        this.fleetValidator = fleetValidator;
        this.characterApi = characterApi;
        this.corporationApi = corporationApi;
    }

    public String inviteCorporation(InviteModel invite, Integer charId, String accessToken) {
        try {
            String charName = getCharName(charId);
            String fleetName = getFleetName(invite.getFleetId(), charId);
            String inviteName = getCorporationName(invite.getCorporationId());
            String inviteKey = inviteService.inviteCorporation(invite.getFleetId(), invite.getCorporationId(), inviteName);
            inviteService.sendInviteCorporationMail(charId, charName, fleetName, invite.getCorporationId(), inviteName, inviteKey, accessToken);
            return inviteKey;
        } catch (ApiException e) {
            throw new HandlerException("unable to send create invitation", e);
        }
    }

    public String inviteCharacter(InviteModel invite, Integer charId, String accessToken) {
        try {
            String charName = getCharName(charId);
            String fleetName = getFleetName(invite.getFleetId(), charId);
            String inviteName = getCharName(invite.getCharacterId());
            String inviteKey = inviteService.inviteCharacter(invite.getFleetId(), invite.getCharacterId(), inviteName);
            inviteService.sendInviteCharacterMail(charId, charName, fleetName, invite.getCharacterId(), inviteName, inviteKey, accessToken);
            return inviteKey;
        } catch (ApiException e) {
            throw new HandlerException("unable to send create invitation", e);
        }
    }

    public InviteModel getInvite(String key, int charId) {
        InviteModel invite = InviteMapper.INSTANCE.dbToModel(inviteService.getInvite(key));
        try {
            if (!inviteValidator.verifyInvite(invite, charId)) {
                throw new SecurityException("Access Denied");
            }
            FleetModel fleet = getFleet(invite.getFleetId(), charId);
            invite.setCharacterName(characterApi.getCharactersCharacterId(fleet.getOwner(), null, null).getName());
            invite.setFleet(fleet);
        } catch (ApiException e) {
            throw new HandlerException("Unable to fetch data from EVE ESI", e);
        }
        return invite;
    }

    private String getFleetName(Integer fleetId, Integer charId) {
        return getFleet(fleetId, charId).getName();
    }

    private String getCharName(Integer characterId) throws ApiException {
        return characterApi.getCharactersCharacterId(characterId, null, null).getName();
    }

    private String getCorporationName(Integer corporationId) throws ApiException {
        return corporationApi.getCorporationsCorporationId(corporationId, null, null).getName();
    }

    private FleetModel getFleet(Integer fleetId, Integer charId) {
        return fleetService.getFleet(fleetId)
                .map(FleetMapper.INSTANCE::dbToModel)
                .filter((f) -> fleetValidator.verifyFleet(f, charId))
                .orElseThrow(() -> new NoSuchEntryException(String.format("No fleet found for id %d", fleetId)));
    }
}
