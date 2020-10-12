package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.FleetManager;
import com.speedment.runtime.core.exception.SpeedmentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class FleetService {

    private final FleetManager fleetManager;

    @Autowired
    public FleetService(FleetManager fleetManager) {
        this.fleetManager = fleetManager;
    }

    public Stream<Fleet> getActiveFleets(Integer corporationId) {
        return fleetManager.stream()
                .filter(fleet -> fleet.getCorporationId().isEmpty() ||
                        fleet.getCorporationId().getAsInt() == 0 ||
                        fleet.getCorporationId().getAsInt() == corporationId)
                .filter(fleet -> fleet
                        .getStartDateTime()
                        // If there is no actual startDateTime, fake one in the future to ensure the fleet is added to the list
                        .orElseGet(() -> Timestamp.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                        .toInstant()
                        // add all fleets which are starting in the future of currently active
                        .plus(fleet.getDuration().orElse(0), ChronoUnit.HOURS)
                        .isAfter(Instant.now()));
    }

    public Stream<Fleet> getAllFleets(Integer corporationId) {
        return fleetManager.stream()
                .filter(fleet -> fleet.getCorporationId().isEmpty() ||
                        fleet.getCorporationId().getAsInt() == 0 ||
                        fleet.getCorporationId().getAsInt() == corporationId);
    }

    public Stream<Fleet> getOwnedFleets(Integer charId) {
        return fleetManager.stream().filter(fleet -> fleet.getOwner() == charId);
    }

    public Stream<Fleet> getActiveOwnedFleets(Integer charId) {
        return fleetManager.stream()
                .filter(fleet -> fleet.getOwner() == charId)
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
}
