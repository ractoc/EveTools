package com.ractoc.eve.fleetmanager.handler;

import com.ractoc.eve.domain.fleetmanager.RoleModel;
import com.ractoc.eve.fleetmanager.mapper.RoleMapper;
import com.ractoc.eve.fleetmanager.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@Slf4j
public class RoleHandler {

    private final RoleService roleService;

    public RoleHandler(RoleService roleService) {
        this.roleService = roleService;
    }

    public List<RoleModel> getRoles() {
        return roleService.getRoles().map(RoleMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }

    public List<RoleModel> getRolesForFleet(Integer fleetId) {
        return roleService.getRolesForFleet(fleetId).map(RoleMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }

    public List<RoleModel> addRoleToFleet(RoleModel role, Integer fleetId) {
        roleService.addRoleToFleet(role.getId(), fleetId, role.getAmount());
        return roleService.getRolesForFleet(fleetId).map(RoleMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }

    public List<RoleModel> removeRoleFromFleet(Integer roleId, Integer fleetId) {
        roleService.removeRoleFromFleet(roleId, fleetId);
        return roleService.getRolesForFleet(fleetId).map(RoleMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }

    public List<RoleModel> updateRoleForFleet(RoleModel role, Integer fleetId) {
        roleService.updateRoleForFleet(role.getId(), fleetId, role.getAmount());
        return roleService.getRolesForFleet(fleetId).map(RoleMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }
}