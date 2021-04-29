package com.ractoc.eve.universe.handler;

import com.ractoc.eve.domain.universe.SolarsystemModel;
import com.ractoc.eve.universe.mapper.SolarsystemMapper;
import com.ractoc.eve.universe.service.SolarsystemService;
import com.speedment.runtime.core.component.transaction.TransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class SolarsystemHandler {

    private final SolarsystemService solarsystemService;
    private TransactionHandler transactionHandler;

    @Autowired
    public SolarsystemHandler(TransactionHandler transactionHandler,
                              SolarsystemService solarsystemService) {
        this.solarsystemService = solarsystemService;
        this.transactionHandler = transactionHandler;
    }

    public List<SolarsystemModel> getSolarsystemList() {
        return solarsystemService.getSolarsystemList().map(SolarsystemMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }

    public SolarsystemModel getSolarsystemById(Integer id) {
        return SolarsystemMapper.INSTANCE.dbToModel(solarsystemService.getSolarsystemById(id));
    }

    public void clearAllSolarSystems() {
        solarsystemService.clearAllSolarSystems();
    }

    public void saveSolarsystems(List<SolarsystemModel> solarsystems) {
        transactionHandler.createAndAccept(tx -> {
            solarsystems.stream()
                    .map(SolarsystemMapper.INSTANCE::modelToDb)
                    .forEach(solarsystemService::saveSolarsystem);
            tx.commit();
        });
    }
}
