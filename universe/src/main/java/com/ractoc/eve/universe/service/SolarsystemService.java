package com.ractoc.eve.universe.service;

import com.ractoc.eve.universe.db.universe.eve_universe.solarsystem.Solarsystem;
import com.ractoc.eve.universe.db.universe.eve_universe.solarsystem.SolarsystemManager;
import com.speedment.runtime.core.exception.SpeedmentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ractoc.eve.universe.db.universe.eve_universe.solarsystem.generated.GeneratedSolarsystem.ID;

@Service
public class SolarsystemService {

    private static final String SOLARSYSTEM_NOT_FOUND = "System with %s %s not found";
    private final SolarsystemManager solarsystemManager;

    @Autowired
    public SolarsystemService(SolarsystemManager solarsystemManager) {
        this.solarsystemManager = solarsystemManager;
    }

    public Stream<Solarsystem> getSolarsystemList() {
        return solarsystemManager.stream();
    }

    public Solarsystem getSolarsystemById(Integer id) {
        Optional<Solarsystem> solarsystem = solarsystemManager.stream()
                .filter(ID.equal(id)).findAny();
        return solarsystem.orElseThrow(() -> new NoSuchEntryException(String.format(SOLARSYSTEM_NOT_FOUND, "id", id)));
    }

    public void saveSolarsystem(Solarsystem solarsystem) {
        try {
            solarsystemManager.persist(solarsystem);
        } catch (SpeedmentException e) {
            throw new ServiceException("Unable to save solarsystem " + solarsystem.getName(), e);
        }
    }

    public void clearAllSolarSystems() {
        List<Solarsystem> solarsystems = solarsystemManager.stream().collect(Collectors.toList());
        solarsystems.forEach(solarsystemManager.remover());
    }
}
