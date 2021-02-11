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

    public boolean verifyFleetInvites(FleetModel fleet, Integer charId) {
        return getInvitesForFleet(fleet).stream().anyMatch(invite -> verifyInvite(fleet, invite, charId));
    }

    public boolean verifyInvite(FleetModel fleet, InvitationModel invite, Integer charId) {
        try {
            if (fleet.getOwner().equals(charId)) {
                return true;
            }
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

    public List<InvitationModel> getInvitesForFleet(FleetModel fleet) {
        return inviteService.getInvitesForFleet(fleet).map(InviteMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }
}
