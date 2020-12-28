package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.FleetManager;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.role_fleet.RoleFleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.role_fleet.RoleFleetManager;
import com.speedment.runtime.core.exception.SpeedmentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Stream;

import static com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.generated.GeneratedFleet.OWNER;

@Service
public class FleetService {

    private final FleetManager fleetManager;
    private final RoleFleetManager roleFleetManager;

    @Autowired
    public FleetService(FleetManager fleetManager,
                        RoleFleetManager roleFleetManager) {
        this.fleetManager = fleetManager;
        this.roleFleetManager = roleFleetManager;
    }

    public Stream<Fleet> getActiveFleets() {
        return fleetManager.stream()
                .filter(fleet -> fleet
                        .getStartDateTime()
                        // If there is no actual startDateTime, fake one in the future to ensure the fleet is added to the list
                        .orElseGet(() -> Timestamp.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                        .toInstant()
                        // add all fleets which are starting in the future of currently active
                        .plus(fleet.getDuration().orElse(0), ChronoUnit.HOURS)
                        .isAfter(Instant.now()));
    }

    public Stream<Fleet> getAllFleets() {
        return fleetManager.stream();
    }

    public Stream<Fleet> getOwnedFleets(Integer charId) {
        return fleetManager.stream().filter(OWNER.equal(charId));
    }

    public Stream<Fleet> getActiveOwnedFleets(Integer charId) {
        return fleetManager.stream()
                .filter(OWNER.equal(charId))
                .filter(fleet -> fleet
                        .getStartDateTime()
                        // If there is no actual startDateTime, fake one in the future to ensure the fleet is added to the list
                        .orElseGet(() -> Timestamp.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                        .toInstant()
                        // add all fleets which are starting in the future of currently active
                        .plus(fleet.getDuration().orElse(0), ChronoUnit.HOURS)
                        .isAfter(Instant.now()));
    }

    public Optional<Fleet> getFleet(int id) {
        return fleetManager.stream().filter(fleet -> fleet.getId() == id).findFirst();
    }

    public Fleet saveFleet(Fleet fleet) {
        try {
            return fleetManager.persist(fleet);
        } catch (SpeedmentException e) {
            throw new ServiceException("Unable to save fleet " + fleet.getName(), e);
        }
    }

    // verifying the existence of the fleet is done in the handler, among other verifications.
    public void updateFleet(Fleet fleet) {
        try {
            fleetManager.update(fleet);
        } catch (SpeedmentException e) {
            throw new ServiceException("Unable to update fleet " + fleet.getId(), e);
        }
    }

    // verifying the existence of the fleet is done in the handler, among other verifications.
    public void deleteFleet(Fleet fleet) {
        try {
            fleetManager.remove(fleet);
        } catch (SpeedmentException e) {
            throw new ServiceException("Unable to delete fleet " + fleet.getId(), e);
        }
    }

    public Stream<RoleFleet> getFleetRoles(Integer fleetId) {
        return roleFleetManager.stream().filter(RoleFleet.FLEET_ID.equal(fleetId));
    }

    public void saveRole(RoleFleet roleFleet) {
        try {
            roleFleetManager.persist(roleFleet);
        } catch (SpeedmentException e) {
            throw new ServiceException("Unable to save roleFleet" + roleFleet.getRoleId() + "-" + roleFleet.getFleetId(), e);
        }
    }

    public void updateRole(RoleFleet roleFleet) {
        try {
            roleFleetManager.update(roleFleet);
        } catch (SpeedmentException e) {
            throw new ServiceException("Unable to update roleFleet" + roleFleet.getRoleId() + "-" + roleFleet.getFleetId(), e);
        }
    }

    public void deleteRole(RoleFleet roleFleet) {
        try {
            roleFleetManager.remove(roleFleet);
        } catch (SpeedmentException e) {
            throw new ServiceException("Unable to delete roleFleet" + roleFleet.getRoleId() + "-" + roleFleet.getFleetId(), e);
        }
    }
}
