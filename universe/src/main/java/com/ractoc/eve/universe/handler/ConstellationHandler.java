package com.ractoc.eve.universe.handler;

import com.ractoc.eve.domain.universe.ConstellationModel;
import com.ractoc.eve.universe.mapper.ConstellationMapper;
import com.ractoc.eve.universe.service.ConstellationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class ConstellationHandler {

    private final ConstellationService constellationService;

    @Autowired
    public ConstellationHandler(ConstellationService constellationService) {
        this.constellationService = constellationService;
    }

    public List<ConstellationModel> getConstellationList() {
        return constellationService.getConstellationList().map(ConstellationMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }

    public ConstellationModel getConstellationById(Integer id) {
        return ConstellationMapper.INSTANCE.dbToModel(constellationService.getConstellationById(id));
    }

    public ConstellationModel saveConstellation(ConstellationModel constellation) {
        return ConstellationMapper.INSTANCE.dbToModel(constellationService.saveConstellation(ConstellationMapper.INSTANCE.modelToDb(constellation)));
    }
}
