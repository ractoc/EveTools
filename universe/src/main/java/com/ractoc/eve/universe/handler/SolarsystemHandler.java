package com.ractoc.eve.universe.handler;

import com.ractoc.eve.domain.universe.SolarsystemModel;
import com.ractoc.eve.universe.mapper.SolarsystemMapper;
import com.ractoc.eve.universe.service.SolarsystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class SolarsystemHandler {

    private final SolarsystemService solarsystemService;

    @Autowired
    public SolarsystemHandler(SolarsystemService solarsystemService) {
        this.solarsystemService = solarsystemService;
    }

    public List<SolarsystemModel> getSolarsystemList() {
        return solarsystemService.getSolarsystemList().map(SolarsystemMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }

    public SolarsystemModel getSolarsystemById(Integer id) {
        return SolarsystemMapper.INSTANCE.dbToModel(solarsystemService.getSolarsystemById(id));
    }

    public SolarsystemModel saveSolarsystem(SolarsystemModel solarsystem) {
        return SolarsystemMapper.INSTANCE.dbToModel(solarsystemService.saveSolarsystem(SolarsystemMapper.INSTANCE.modelToDb(solarsystem)));
    }
}
