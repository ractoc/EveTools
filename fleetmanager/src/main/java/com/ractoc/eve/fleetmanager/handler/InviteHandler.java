package com.ractoc.eve.fleetmanager.handler;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.domain.fleetmanager.InvitationModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invite.Invite;
import com.ractoc.eve.fleetmanager.mapper.FleetMapper;
import com.ractoc.eve.fleetmanager.mapper.InviteMapper;
import com.ractoc.eve.fleetmanager.service.FleetService;
import com.ractoc.eve.fleetmanager.service.InviteService;
import com.ractoc.eve.fleetmanager.service.NoSuchEntryException;
import com.ractoc.eve.fleetmanager.validator.FleetValidator;
import com.ractoc.eve.fleetmanager.validator.InviteValidator;
import com.ractoc.eve.fleetmanager.validator.RegistrationValidator;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.CharacterApi;
import com.ractoc.eve.jesi.api.CorporationApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@Slf4j
public class InviteHandler {

    public static final String ACCESS_DENIED = "Access Denied";
    private final InviteService inviteService;
    private final FleetService fleetService;
    private final InviteValidator inviteValidator;
    private final FleetValidator fleetValidator;
    private final RegistrationValidator registrationValidator;
    private final CharacterApi characterApi;
    private final CorporationApi corporationApi;

    @Autowired
    public InviteHandler(InviteService inviteService,
                         FleetService fleetService,
                         InviteValidator inviteValidator,
                         FleetValidator fleetValidator,
                         RegistrationValidator registrationValidator,
                         CharacterApi characterApi,
                         CorporationApi corporationApi) {
        this.inviteService = inviteService;
        this.fleetService = fleetService;
        this.inviteValidator = inviteValidator;
        this.fleetValidator = fleetValidator;
        this.registrationValidator = registrationValidator;
        this.characterApi = characterApi;
        this.corporationApi = corporationApi;
    }

    public List<InvitationModel> invite(Integer fleetId, InvitationModel invitation, Integer charId) {
        try {
            String charName = getCharName(charId);
            String inviteeName = getInviteeName(invitation);
            String fleetName = getFleetName(fleetId, charId);
            Fleet fleet = fleetService.getFleet(fleetId).orElseThrow(() -> new NoSuchEntryException("No fleet found linked to invitation."));
            String inviteKey = inviteService.invite(fleetId, invitation.getId(), invitation.getType());
            inviteService.sendInviteMail(charId,
                    charName,
                    fleetId,
                    fleetName,
                    invitation.getId(),
                    invitation.getType(),
                    inviteeName,
                    inviteKey,
                    fleet.getDescription().orElse(""));
            return getInvitesForFleet(fleetId, charId);
        } catch (ApiException | com.ractoc.eve.user_client.ApiException e) {
            throw new HandlerException("unable to send create invitation", e);
        }
    }

    public InvitationModel getInvite(String key, int charId) {
        InvitationModel invite = InviteMapper.INSTANCE.dbToModel(inviteService.getInvite(key));
        FleetModel fleet = getFleet(invite.getFleetId(), charId);
        if (!inviteValidator.verifyInvite(fleet, invite, charId)) {
            throw new SecurityException(ACCESS_DENIED);
        }
        return invite;
    }

    public List<InvitationModel> getInvitesForCharacter(int charId) {
        try {
            Integer corpId = characterApi.getCharactersCharacterId(charId, null, null).getCorporationId();
            return inviteService.getInvitesForCharacter(charId, corpId)
                    .filter(invite -> this.filterRegistrations(invite, charId))
                    .map(InviteMapper.INSTANCE::dbToModel)
                    .collect(Collectors.toList());
        } catch (ApiException e) {
            throw new HandlerException("Unable to fetch data from EVE ESI", e);
        }
    }

    private boolean filterRegistrations(Invite invite, Integer charId) {
        // in case of a corporation invite we need to check the registration
        if (invite.getType().equals("corporation")) {
            return !registrationValidator.hasRegistration(invite.getFleetId(), charId);
        }
        return true;
    }

    public List<InvitationModel> getInvitesForFleet(Integer fleetId, int charId) {
        Fleet fleet = fleetService.getFleet(fleetId).orElseThrow(() -> new NoSuchEntryException("fleet not found"));
        if (fleetValidator.verifyFleet(FleetMapper.INSTANCE.dbToModel(fleet), charId)) {
            return inviteService.getInvitesForFleet(fleetId).map(InviteMapper.INSTANCE::dbToModel).collect(Collectors.toList());
        } else {
            throw new SecurityException(ACCESS_DENIED);
        }
    }

    public List<InvitationModel> deleteInvite(Integer id, int charId) {
        Invite invite = inviteService.getInvite(id);
        Fleet fleet = fleetService.getFleet(invite.getFleetId()).orElseThrow(() -> new NoSuchEntryException("fleet not found"));
        if (fleet.getOwner() == charId) {
            inviteService.deleteInvitation(invite);
            return getInvitesForFleet(fleet.getId(), charId);
        } else {
            throw new SecurityException(ACCESS_DENIED);
        }
    }

    private String getFleetName(Integer fleetId, Integer charId) {
        return getFleet(fleetId, charId).getName();
    }

    private String getInviteeName(InvitationModel invitation) throws ApiException {
        if (invitation.getType().equals("character")) {
            return getCharName(invitation.getId());
        } else {
            return getCorpName(invitation.getId());
        }
    }

    private String getCharName(Integer characterId) throws ApiException {
        return characterApi.getCharactersCharacterId(characterId, null, null).getName();
    }

    private String getCorpName(Integer corporationId) throws ApiException {
        return corporationApi.getCorporationsCorporationId(corporationId, null, null).getName();
    }

    private FleetModel getFleet(Integer fleetId, Integer charId) {
        return fleetService.getFleet(fleetId)
                .map(FleetMapper.INSTANCE::dbToModel)
                .filter(f -> fleetValidator.verifyFleet(f, charId))
                .orElseThrow(() -> new NoSuchEntryException(String.format("No fleet found for id %d", fleetId)));
    }
}
