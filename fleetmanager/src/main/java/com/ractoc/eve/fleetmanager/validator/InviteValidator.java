package com.ractoc.eve.fleetmanager.validator;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.domain.fleetmanager.InviteModel;
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
        return getInvitesForFleet(fleet).stream().anyMatch(invite -> verifyInvite(invite, charId));
    }

    public boolean verifyInvite(InviteModel invite, Integer charId) {
        try {
            if (invite.getCharacterId() != 0) {
                if (!invite.getCharacterId().equals(charId)) {
                    return false;
                }
            } else if (invite.getCorporationId() != null) {
                Integer corpId = characterApi.getCharactersCharacterId(charId, null, null).getCorporationId();
                if (!invite.getCorporationId().equals(corpId)) {
                    return false;
                }
            }
            return true;
        } catch (ApiException e) {
            throw new HandlerException("Unable to fetch data from EVE ESI", e);
        }
    }

    public List<InviteModel> getInvitesForFleet(FleetModel fleet) {
        return inviteService.getInvitesForFleet(fleet).map(InviteMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }
}
