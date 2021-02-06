package com.ractoc.eve.fleetmanager.handler;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.domain.fleetmanager.RoleModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.registrations.generated.GeneratedRegistrations;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.role_fleet.RoleFleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.role_fleet.RoleFleetImpl;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.role_fleet.generated.GeneratedRoleFleet;
import com.ractoc.eve.fleetmanager.mapper.FleetMapper;
import com.ractoc.eve.fleetmanager.model.FleetSearchParams;
import com.ractoc.eve.fleetmanager.service.*;
import com.ractoc.eve.fleetmanager.validator.FleetValidator;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.CharacterApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
public class FleetHandler {

    private final FleetService fleetService;
    private final FleetValidator fleetValidator;
    private final InviteService inviteService;
    private final RegistrationService registrationService;
    private final RoleService roleService;
    private final CharacterApi characterApi;

    @Autowired
    public FleetHandler(FleetService fleetService,
                        FleetValidator fleetValidator,
                        InviteService inviteService,
                        RegistrationService registrationService,
                        RoleService roleService,
                        CharacterApi characterApi) {
        this.fleetService = fleetService;
        this.fleetValidator = fleetValidator;
        this.inviteService = inviteService;
        this.registrationService = registrationService;
        this.roleService = roleService;
        this.characterApi = characterApi;
    }

    public List<FleetModel> searchFleets(FleetSearchParams params, int charId) {
        try {
            Integer corpId = characterApi.getCharactersCharacterId(charId, null, null).getCorporationId();
            return fleetService.searchFleets(params, charId, corpId).map(FleetMapper.INSTANCE::dbToModel).collect(Collectors.toList());
        } catch (ApiException e) {
            throw new HandlerException("Unable to fetch data from EVE ESI", e);
        }
    }

    public FleetModel getFleet(Integer id, Integer charId) {
        FleetModel fleet = fleetService.getFleet(id)
                .map(FleetMapper.INSTANCE::dbToModel)
                .filter(f -> fleetValidator.verifyFleet(f, charId))
                .orElseThrow(() -> new NoSuchEntryException(String.format("No fleet found for id %d", id)));
        return fleet;
    }

    public FleetModel saveFleet(FleetModel fleet, Integer charId) {
        fleet.setOwner(charId);
        return FleetMapper.INSTANCE.dbToModel(fleetService.saveFleet(FleetMapper.INSTANCE.modelToDb(fleet)));
    }

    public void updateFleet(FleetModel fleet, Integer charId) {
        Fleet verifyFleet = fleetService.getFleet(fleet.getId()).orElseThrow(() -> new NoSuchEntryException("fleet not found for id " + fleet.getId()));
        if (verifyFleet.getOwner() == charId) {
            fleetService.updateFleet(FleetMapper.INSTANCE.modelToDb(fleet));
        } else {
            throw new SecurityException("Requesting charId not owner of fleet");
        }
    }

    public void deleteFleet(Integer fleetId, Integer charId) {
        Fleet dbFleet = fleetService.getFleet(fleetId).orElseThrow(() -> new NoSuchEntryException("fleet not found for id " + fleetId));
        if (dbFleet.getOwner() == charId) {
            // remove invites
            inviteService.getInvitesForFleet(fleetId).forEach(inviteService::deleteInvitation);
            // remove registrations
            registrationService.getRegistrationsForFleet(fleetId).forEach(registrationService::deleteRegistration);
            // remove roles
            roleService.getRolesForFleet(fleetId).forEach(role -> roleService.removeRoleFromFleet(role.getId(), fleetId));
            // remove fleet
            fleetService.deleteFleet(dbFleet);
        } else {
            throw new SecurityException("Requesting charId not owner of fleet");
        }
    }
}
