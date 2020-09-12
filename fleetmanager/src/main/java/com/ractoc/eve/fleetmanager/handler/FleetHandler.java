package com.ractoc.eve.fleetmanager.handler;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.domain.fleetmanager.InviteModel;
import com.ractoc.eve.domain.fleetmanager.SimpleFleetModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import com.ractoc.eve.fleetmanager.mapper.FleetMapper;
import com.ractoc.eve.fleetmanager.mapper.SimpleFleetMapper;
import com.ractoc.eve.fleetmanager.service.FleetService;
import com.ractoc.eve.fleetmanager.service.NoSuchEntryException;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.CharacterApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class FleetHandler {

    private final FleetService fleetService;
    private final InviteHandler inviteHandler;
    private final CharacterApi characterApi;

    @Autowired
    public FleetHandler(FleetService fleetService, InviteHandler inviteHandler, CharacterApi characterApi) {
        this.fleetService = fleetService;
        this.inviteHandler = inviteHandler;
        this.characterApi = characterApi;
    }

    // charId will be used to verify permission to display a fleet in the list
    public List<SimpleFleetModel> getFleetList(Integer charId) {
        try {
            Integer corporationId = characterApi.getCharactersCharacterId(charId, null, null).getCorporationId();
            return fleetService.getFleets(charId, corporationId).map(SimpleFleetMapper.INSTANCE::dbToModel).collect(Collectors.toList());
        } catch (ApiException e) {
            throw new HandlerException("Unable to resolve corporationId for character " + charId);
        }
    }

    // charId will be used to verify permission to access fleet data
    public FleetModel getFleet(Integer id, Integer charId) {
        Fleet dbFleet = fleetService.getFleet(id)
                .orElseThrow(() -> new NoSuchEntryException("no fleet found for id " + id));
        FleetModel fleet = FleetMapper.INSTANCE.dbToModel(dbFleet);
        fleet.setCorporationRestricted(dbFleet.getCorporationId().isPresent() && dbFleet.getCorporationId().getAsInt() > 0);
        return fleet;
    }

    public FleetModel saveFleet(FleetModel fleet, Integer charId, String accessToken) {
        fleet.setOwner(charId);
        Fleet dbFleet = FleetMapper.INSTANCE.modelToDb(fleet);
        FleetModel savedFleet = null;
        if (fleet.isCorporationRestricted()) {
            // inject corporation id if needed
            try {
                Integer corporationId = characterApi.getCharactersCharacterId(charId, null, null).getCorporationId();
                dbFleet.setCorporationId(corporationId);
                savedFleet = FleetMapper.INSTANCE.dbToModel(fleetService.saveFleet(dbFleet));
                // send the invitation
                inviteHandler.inviteCorporation(
                        InviteModel.builder()
                                .fleetId(savedFleet.getId())
                                .name(savedFleet.getName())
                                .corporationId(corporationId).build(),
                        charId,
                        accessToken);
            } catch (ApiException e) {
                throw new HandlerException("Unable to resolve corporationId for character " + charId);
            }
        } else {
            dbFleet.setCorporationId(null);
            savedFleet = FleetMapper.INSTANCE.dbToModel(fleetService.saveFleet(dbFleet));
        }
        return savedFleet;
    }

    public FleetModel updateFleet(FleetModel fleet, Integer charId) {
        Fleet verifyFleet = fleetService.getFleet(fleet.getId()).orElseThrow(() -> new NoSuchEntryException("fleet not found for id " + fleet.getId()));
        if (verifyFleet.getOwner() == charId) {
            Fleet dbFleet = FleetMapper.INSTANCE.modelToDb(fleet);
            if (fleet.isCorporationRestricted()) {
                // inject corporation id if needed
                try {
                    dbFleet.setCorporationId(characterApi.getCharactersCharacterId(charId, null, null).getCorporationId());
                } catch (ApiException e) {
                    throw new HandlerException("Unable to resolve corporationId for character " + charId);
                }
            } else {
                dbFleet.setCorporationId(null);
            }
            fleetService.updateFleet(dbFleet);
            return fleet;
        } else {
            throw new SecurityException("Requesting charId not owner of fleet");
        }
    }

    public void deleteFleet(Integer id, Integer charId) {
        Fleet dbFleet = fleetService.getFleet(id).orElseThrow(() -> new NoSuchEntryException("fleet not found for id " + id));
        if (dbFleet.getOwner() == charId) {
            fleetService.deleteFleet(dbFleet);
        } else {
            throw new SecurityException("Requesting charId not owner of fleet");
        }
    }
}
