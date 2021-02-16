package com.ractoc.eve.fleetmanager.validator;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.domain.fleetmanager.InvitationModel;
import com.ractoc.eve.fleetmanager.handler.HandlerException;
import com.ractoc.eve.fleetmanager.mapper.InviteMapper;
import com.ractoc.eve.fleetmanager.service.InviteService;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.CharacterApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InviteValidator {

    private final InviteService inviteService;
    private final CharacterApi characterApi;

    @Autowired
    public InviteValidator(InviteService inviteService, CharacterApi characterApi) {
        this.inviteService = inviteService;
        this.characterApi = characterApi;
    }

    public boolean verifyFleetInvites(FleetModel fleet, Integer charId, Integer corpId) {
        if (fleet.getOwner().equals(charId)) {
            return true;
        }
        return getInvitesForFleet(fleet, charId, corpId).stream().findFirst().isPresent();
    }

    public boolean verifyInvite(FleetModel fleet, InvitationModel invite, Integer charId) {
        if (fleet.getOwner().equals(charId)) {
            return true;
        }
        try {
            if (invite.getType().equals("character")) {
                return invite.getId().equals(charId);
            } else {
                Integer corpId = characterApi.getCharactersCharacterId(charId, null, null).getCorporationId();
                return invite.getId().equals(corpId);
            }
        } catch (ApiException e) {
            throw new HandlerException("Unable to fetch data from EVE ESI", e);
        }
    }

    public boolean verifyInvite(FleetModel fleet, InvitationModel invite, Integer charId, Integer corpId) {
        if (fleet.getOwner().equals(charId)) {
            return true;
        }
        if (invite.getType().equals("character")) {
            return invite.getId().equals(charId);
        } else {
            return invite.getId().equals(corpId);
        }
    }

    public List<InvitationModel> getInvitesForFleet(FleetModel fleet, Integer charId, Integer corpId) {
        return inviteService.getInvitesForFleetAndCharacter(fleet.getId(), charId, corpId).map(InviteMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }
}
