package com.ractoc.eve.fleetmanager.service;

import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.FleetManager;
import com.speedment.runtime.core.exception.SpeedmentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class FleetService {

    private final FleetManager fleetManager;

    @Autowired
    public FleetService(FleetManager fleetManager) {
        this.fleetManager = fleetManager;
    }

    public Stream<Fleet> getFleets(Integer charId, Integer corporationId) {
        return fleetManager.stream()
                .filter(fleet -> fleet.getCorporationId().isEmpty() ||
                        fleet.getCorporationId().getAsInt() == 0 ||
                        fleet.getCorporationId().getAsInt() == corporationId);
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
