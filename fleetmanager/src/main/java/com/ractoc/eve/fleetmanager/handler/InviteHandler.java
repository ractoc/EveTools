package com.ractoc.eve.fleetmanager.handler;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.domain.fleetmanager.InviteModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.invites.Invites;
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
import com.ractoc.eve.jesi.model.PostCharactersCharacterIdMailRecipient.RecipientTypeEnum;
import com.speedment.common.tuple.Tuple2;
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

    @Autowired
    public InviteHandler(InviteService inviteService,
                         FleetService fleetService,
                         InviteValidator inviteValidator,
                         FleetValidator fleetValidator,
                         RegistrationValidator registrationValidator,
                         CharacterApi characterApi) {
        this.inviteService = inviteService;
        this.fleetService = fleetService;
        this.inviteValidator = inviteValidator;
        this.fleetValidator = fleetValidator;
        this.registrationValidator = registrationValidator;
        this.characterApi = characterApi;
    }

    public String invite(InviteModel invite, Integer charId, String accessToken) {
        try {
            String charName = getCharName(charId);
            String fleetName = getFleetName(invite.getFleetId(), charId);
            Fleet fleet = fleetService.getFleet(invite.getFleetId()).orElseThrow(() -> new NoSuchEntryException("No fleet found linked to invitation."));
            String inviteKey = inviteService.invite(invite.getFleetId(), invite.getCharId(), invite.getCorpId(), invite.getName());
            inviteService.sendInviteMail(charId,
                    charName,
                    fleetName,
                    invite.getCharId() != null ? invite.getCharId() : invite.getCorpId(),
                    invite.getName(),
                    invite.getCharId() != null ? RecipientTypeEnum.CHARACTER : RecipientTypeEnum.CORPORATION,
                    inviteKey,
                    fleet.getDescription().orElse(""),
                    accessToken);
            return inviteKey;
        } catch (ApiException e) {
            throw new HandlerException("unable to send create invitation", e);
        }
    }

    public InviteModel getInvite(String key, int charId) {
        InviteModel invite = InviteMapper.INSTANCE.dbToModel(inviteService.getInvite(key));
        FleetModel fleet = getFleet(invite.getFleetId(), charId);
        invite.setFleet(fleet);
        if (!inviteValidator.verifyInvite(invite, charId)) {
            throw new SecurityException(ACCESS_DENIED);
        }
        return invite;
    }

    public List<InviteModel> getInvitesForCharacter(int charId) {
        try {
            Integer corpId = characterApi.getCharactersCharacterId(charId, null, null).getCorporationId();
            return inviteService.getInvitesForCharacter(charId, corpId)
                    .filter(inviteFleet -> this.filterRegistrations(inviteFleet, charId))
                    .map(InviteMapper.INSTANCE::joinToModel)
                    .collect(Collectors.toList());
        } catch (ApiException e) {
            throw new HandlerException("Unable to fetch data from EVE ESI", e);
        }
    }

    private boolean filterRegistrations(Tuple2<Invites, Fleet> inviteFleet, Integer charId) {
        // in case of a corporation invite we need to check the registration
        if (((Invites) inviteFleet.get(0)).getCorpId().isPresent()) {
            return !registrationValidator.hasRegistration(((Fleet) inviteFleet.get(1)).getId(), charId);
        }
        return true;
    }

    public List<InviteModel> getInvitesForFleet(Integer fleetId, int charId) {
        Fleet fleet = fleetService.getFleet(fleetId).orElseThrow(() -> new NoSuchEntryException("fleet not found"));
        if (fleetValidator.verifyFleet(FleetMapper.INSTANCE.dbToModel(fleet), charId)) {
            return inviteService.getInvitesForFleet(fleetId).map(InviteMapper.INSTANCE::dbToModel).collect(Collectors.toList());
        } else {
            throw new SecurityException(ACCESS_DENIED);
        }
    }

    public void deleteInvite(String key, int charId) {
        Invites invite = inviteService.getInvite(key);
        Fleet fleet = fleetService.getFleet(invite.getFleetId()).orElseThrow(() -> new NoSuchEntryException("fleet not found"));
        if (fleet.getOwner() == charId) {
            inviteService.deleteInvitation(invite);
        } else {
            throw new SecurityException(ACCESS_DENIED);
        }
    }

    private String getFleetName(Integer fleetId, Integer charId) {
        return getFleet(fleetId, charId).getName();
    }

    private String getCharName(Integer characterId) throws ApiException {
        return characterApi.getCharactersCharacterId(characterId, null, null).getName();
    }

    private FleetModel getFleet(Integer fleetId, Integer charId) {
        return fleetService.getFleet(fleetId)
                .map(FleetMapper.INSTANCE::dbToModel)
                .filter(f -> fleetValidator.verifyFleet(f, charId))
                .orElseThrow(() -> new NoSuchEntryException(String.format("No fleet found for id %d", fleetId)));
    }
}
