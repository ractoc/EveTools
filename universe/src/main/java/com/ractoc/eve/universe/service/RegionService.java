package com.ractoc.eve.universe.service;

import com.ractoc.eve.universe.db.universe.eve_universe.region.Region;
import com.ractoc.eve.universe.db.universe.eve_universe.region.RegionManager;
import com.speedment.runtime.core.exception.SpeedmentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

import static com.ractoc.eve.universe.db.universe.eve_universe.region.generated.GeneratedRegion.ID;


@Service
public class RegionService {

    private static final String REGION_NOT_FOUND = "Region with %s %s not found";
    private final RegionManager regionManager;

    @Autowired
    public RegionService(RegionManager regionManager) {
        this.regionManager = regionManager;
    }

    public Stream<Region> getRegionList() {
        return regionManager.stream();
    }

    public Region getRegionById(Integer id) {
        Optional<Region> region = regionManager.stream()
                .filter(ID.equal(id)).findAny();
        return region.orElseThrow(() -> new NoSuchEntryException(String.format(REGION_NOT_FOUND, "id", id)));
    }

    public Region saveRegion(Region region) {
        try {
            return insertOrUpdateRegion(region);
        } catch (SpeedmentException e) {
            throw new ServiceException("Unable to save region " + region.getName(), e);
        }
    }

    private Region insertOrUpdateRegion(Region region) {
        try {
            getRegionById(region.getId());
            // if a region is found, update
            return regionManager.update(region);
        } catch (NoSuchEntryException nse) {
            // if no region is found, persist
            return regionManager.persist(region);
        }
    }
}
