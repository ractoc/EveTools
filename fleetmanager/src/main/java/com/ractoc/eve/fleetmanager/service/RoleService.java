package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.role.Role;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.role.RoleManager;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.role_types.RoleTypes;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.role_types.RoleTypesManager;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;


@Service
public class RoleService {

    private final RoleManager roleManager;
    private final RoleTypesManager roleTypesManager;

    public RoleService(RoleManager roleManager, RoleTypesManager roleTypesManager) {
        this.roleManager = roleManager;
        this.roleTypesManager = roleTypesManager;
    }

    public Stream<Role> getRolesForTypesId(Integer typesId) {
        return roleTypesManager.stream().filter(RoleTypes.TYPE_ID.equal(typesId)).map(this::getRole);
    }

    private Role getRole(RoleTypes roleTypes) {
        return roleManager.stream().filter(Role.ID.equal(roleTypes.getRoleId())).findFirst().orElseThrow(() -> new NoSuchEntryException("Unable to find matching Role"));
    }
}
