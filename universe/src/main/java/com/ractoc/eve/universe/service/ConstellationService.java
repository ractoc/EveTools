package com.ractoc.eve.universe.service;

import com.ractoc.eve.universe.db.universe.eve_universe.constellation.Constellation;
import com.ractoc.eve.universe.db.universe.eve_universe.constellation.ConstellationManager;
import com.speedment.runtime.core.exception.SpeedmentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

import static com.ractoc.eve.universe.db.universe.eve_universe.constellation.generated.GeneratedConstellation.ID;


@Service
public class ConstellationService {

    private static final String CONSTELLATION_NOT_FOUND = "Constellation with %s %s not found";
    private final ConstellationManager constellationManager;

    @Autowired
    public ConstellationService(ConstellationManager constellationManager) {
        this.constellationManager = constellationManager;
    }

    public Stream<Constellation> getConstellationList() {
        return constellationManager.stream();
    }

    public Constellation getConstellationById(Integer id) {
        Optional<Constellation> constellation = constellationManager.stream()
                .filter(ID.equal(id)).findAny();
        return constellation.orElseThrow(() -> new NoSuchEntryException(String.format(CONSTELLATION_NOT_FOUND, "id", id)));
    }

    public Constellation saveConstellation(Constellation constellation) {
        try {
            return insertOrUpdateConstellation(constellation);
        } catch (SpeedmentException e) {
            throw new ServiceException("Unable to save constellation " + constellation.getName(), e);
        }
    }

    private Constellation insertOrUpdateConstellation(Constellation constellation) {
        try {
            getConstellationById(constellation.getId());
            // if a constellation is found, update
            return constellationManager.update(constellation);
        } catch (NoSuchEntryException nse) {
            // if no constellation is found, persist
            return constellationManager.persist(constellation);
        }
    }
}
