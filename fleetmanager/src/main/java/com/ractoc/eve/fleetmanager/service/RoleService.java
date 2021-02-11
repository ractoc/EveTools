package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.fleetmanager.db.FleetmanagerApplication;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.role.Role;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.role.RoleManager;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.role_fleet.RoleFleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.role_fleet.RoleFleetImpl;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.role_fleet.RoleFleetManager;
import com.speedment.common.tuple.nullable.Tuple2OfNullables;
import com.speedment.runtime.join.JoinComponent;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;


@Service
public class RoleService {

    private final RoleManager roleManager;
    private final RoleFleetManager roleFleetManager;

    private final JoinComponent joinComponent;

    public RoleService(FleetmanagerApplication app, RoleManager roleManager, RoleFleetManager roleFleetManager) {
        this.roleManager = roleManager;
        this.roleFleetManager = roleFleetManager;
        this.joinComponent = app.getOrThrow(JoinComponent.class);
    }

    public Stream<Role> getRoles(Integer fleetId) {
        if (fleetId == null) {
            return roleManager.stream();
        }
        return joinComponent.from(RoleManager.IDENTIFIER)
                .leftJoinOn(RoleFleet.ROLE_ID).equal(Role.ID)
                .where(RoleFleet.FLEET_ID.notEqual(fleetId))
                .build()
                .stream()
                .map(Tuple2OfNullables::getOrNull0);
    }

    public Stream<Role> getRolesForFleet(Integer fleetId) {
        return roleFleetManager.stream()
                .filter(RoleFleet.FLEET_ID.equal(fleetId)).map(this::getRole);
    }

    public void addRoleToFleet(Integer roleId, Integer fleetId, Integer amount) {
        RoleFleet roleFleet = new RoleFleetImpl();
        roleFleet.setRoleId(roleId);
        roleFleet.setFleetId(fleetId);
        roleFleet.setNumber(amount);
        roleFleetManager.persist(roleFleet);
    }

    public void removeRoleFromFleet(Integer roleId, Integer fleetId) {
        RoleFleet roleFleet = getRoleFleet(roleId, fleetId);
        roleFleetManager.remove(roleFleet);
    }

    public void updateRoleForFleet(Integer roleId, Integer fleetId, Integer amount) {
        RoleFleet roleFleet = getRoleFleet(roleId, fleetId);
        roleFleet.setNumber(amount);
        roleFleetManager.update(roleFleet);
    }

    private Role getRole(RoleFleet roleFleet) {
        return roleManager.stream()
                .filter(Role.ID.equal(roleFleet.getRoleId()))
                .findFirst().orElseThrow(() -> new NoSuchEntryException("Unable to find matching Role"));
    }

    private RoleFleet getRoleFleet(Integer roleId, Integer fleetId) {
        return roleFleetManager.stream()
                .filter(RoleFleet.ROLE_ID.equal(roleId)
                        .and(RoleFleet.FLEET_ID.equal(fleetId)))
                .findFirst().orElseThrow(() -> new NoSuchEntryException("Role not found for fleet"));
    }
}
