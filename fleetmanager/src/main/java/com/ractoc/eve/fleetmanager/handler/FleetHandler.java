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
import com.ractoc.eve.fleetmanager.service.FleetService;
import com.ractoc.eve.fleetmanager.service.InviteService;
import com.ractoc.eve.fleetmanager.service.NoSuchEntryException;
import com.ractoc.eve.fleetmanager.service.RegistrationService;
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
    private final CharacterApi characterApi;

    @Autowired
    public FleetHandler(FleetService fleetService,
                        FleetValidator fleetValidator,
                        InviteService inviteService,
                        RegistrationService registrationService,
                        CharacterApi characterApi) {
        this.fleetService = fleetService;
        this.fleetValidator = fleetValidator;
        this.inviteService = inviteService;
        this.registrationService = registrationService;
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
        fleet.setRoles(fleetService.getFleetRoles(id).map(role -> fleetRoleToRoleModel(role, fleet.getType().getRoles())).collect(Collectors.toList()));
        return fleet;
    }

    private RoleModel fleetRoleToRoleModel(RoleFleet fleetRole, List<RoleModel> roles) {
        return RoleModel.builder()
                .id(fleetRole.getRoleId())
                .amount(fleetRole.getNumber())
                .name(roles.stream()
                        .filter(r -> fleetRole.getRoleId() == r.getId())
                        .map(RoleModel::getName)
                        .findFirst()
                        .orElseThrow(() -> new NoSuchEntryException("role not found")))
                .build();
    }

    public FleetModel saveFleet(FleetModel fleet, Integer charId) {
        fleet.setOwner(charId);
        return FleetMapper.INSTANCE.dbToModel(fleetService.saveFleet(FleetMapper.INSTANCE.modelToDb(fleet)));
    }

    public void updateFleet(FleetModel fleet, Integer charId) {
        Fleet verifyFleet = fleetService.getFleet(fleet.getId()).orElseThrow(() -> new NoSuchEntryException("fleet not found for id " + fleet.getId()));
        if (verifyFleet.getOwner() == charId) {
            fleetService.updateFleet(FleetMapper.INSTANCE.modelToDb(fleet));
            // update fleet roles
            updateFleetRoles(((Fleet) FleetMapper.INSTANCE.modelToDb(fleet)).getId(), fleet.getRoles());
        } else {
            throw new SecurityException("Requesting charId not owner of fleet");
        }
    }

    private void updateFleetRoles(int fleetId, List<RoleModel> roles) {
        // get current fleet roles
        List<RoleFleet> currentRoles = fleetService.getFleetRoles(fleetId).collect(Collectors.toList());
        // if no roles list is provided, remove all current roles
        if (roles == null) {
            currentRoles.stream().forEach(fleetService::deleteRole);
        } else {
            Set<Integer> currentRoleIds = currentRoles.stream().map(GeneratedRoleFleet::getRoleId).collect(Collectors.toSet());
            // get new role ids
            Set<Integer> newRoleIds = roles.stream().map(RoleModel::getId).collect(Collectors.toSet());
            // get fleet roles with pilots, not supported yet
            Set<Integer> registeredRoleIds = registrationService.getRegistrationsForFleet(fleetId)
                    .map(GeneratedRegistrations::getRoleId)
                    .filter(OptionalInt::isPresent)
                    .map(OptionalInt::getAsInt)
                    .collect(Collectors.toSet());

            // update amount of current fleet roles according to new fleet roles
            roles.stream().filter(r -> currentRoleIds.contains(r.getId())).map(r -> this.mapRoleFleet(fleetId, r)).forEach(fleetService::updateRole);
            // add new fleet roles not in current fleet roles
            roles.stream().filter(r -> !currentRoleIds.contains(r.getId())).map(r -> this.mapRoleFleet(fleetId, r)).forEach(fleetService::saveRole);
            // remove current fleet roles with no pilots not in new fleet roles
            currentRoles.stream()
                    .filter(r -> !newRoleIds.contains(r.getRoleId()))
                    .filter(r -> !registeredRoleIds.contains(r.getRoleId()))
                    .forEach(fleetService::deleteRole);
        }
    }

    private RoleFleet mapRoleFleet(int fleetId, RoleModel roleModel) {
        RoleFleet roleFleet = new RoleFleetImpl();
        roleFleet.setRoleId(roleModel.getId());
        roleFleet.setFleetId(fleetId);
        roleFleet.setNumber(roleModel.getAmount());
        return roleFleet;
    }

    public void deleteFleet(Integer fleetId, Integer charId) {
        Fleet dbFleet = fleetService.getFleet(fleetId).orElseThrow(() -> new NoSuchEntryException("fleet not found for id " + fleetId));
        if (dbFleet.getOwner() == charId) {
            // remove invites
            inviteService.getInvitesForFleet(fleetId).forEach(inviteService::deleteInvitation);
            // remove registrations
            registrationService.getRegistrationsForFleet(fleetId).forEach(registrationService::deleteRegistration);
            // remove roles
            fleetService.getFleetRoles(fleetId).forEach(fleetService::deleteRole);
            // remove fleet
            fleetService.deleteFleet(dbFleet);
        } else {
            throw new SecurityException("Requesting charId not owner of fleet");
        }
    }
}
