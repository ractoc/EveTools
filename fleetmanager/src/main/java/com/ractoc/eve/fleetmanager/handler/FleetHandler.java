package com.ractoc.eve.fleetmanager.handler;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import com.ractoc.eve.fleetmanager.mapper.FleetMapper;
import com.ractoc.eve.fleetmanager.service.FleetService;
import com.ractoc.eve.fleetmanager.service.NoSuchEntryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class FleetHandler {

    private final FleetService fleetService;

    @Autowired
    public FleetHandler(FleetService fleetService) {
        this.fleetService = fleetService;
    }

    // charId will be used to veryfy permission to display a fleet in the list
    public List<FleetModel> getFleetList(Integer charId) {
        return fleetService.getFleets().map(FleetMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }

    // charId will be used to verify permission to access fleet data
    public FleetModel getFleet(Integer id, Integer charId) {
        return FleetMapper.INSTANCE.dbToModel(fleetService.getFleet(id).orElseThrow(() -> new NoSuchEntryException("no fleet found for id " + id)));
    }

    public FleetModel saveFleet(FleetModel fleet, Integer charId) {
        fleet.setOwner(charId);
        return FleetMapper.INSTANCE.dbToModel(fleetService.saveFleet(FleetMapper.INSTANCE.modelToDb(fleet)));
    }

    public FleetModel updateFleet(FleetModel fleet, Integer charId) {
        Fleet verifyFleet = fleetService.getFleet(fleet.getId()).orElseThrow(() -> new NoSuchEntryException("fleet not found for id " + fleet.getId()));
        if (verifyFleet.getOwner() == charId) {
            fleetService.updateFleet(FleetMapper.INSTANCE.modelToDb(fleet));
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
