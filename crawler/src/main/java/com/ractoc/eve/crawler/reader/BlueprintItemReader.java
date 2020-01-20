package com.ractoc.eve.crawler.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ractoc.eve.assets.handler.BlueprintHandler;
import com.ractoc.eve.domain.assets.BlueprintModel;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlueprintItemReader implements ItemReader<BlueprintModel> {

    private int nextBlueprintIndex = 0;
    private List<BlueprintModel> blueprints;

    @Autowired
    private BlueprintHandler handler;

    public BlueprintModel read() {
        if (blueprints == null) {
            init();
        }
        BlueprintModel blueprint = null;
        if (nextBlueprintIndex < blueprints.size()) {
            blueprint = blueprints.get(nextBlueprintIndex);
            nextBlueprintIndex++;
        }
        return blueprint;
    }

    private void init() {
        blueprints = fetchBlueprintsFromYML();
        handler.clearAllBlueprints();
    }

    private List<BlueprintModel> fetchBlueprintsFromYML() {
        File file = new File("D:/tmp/sde/fsd/blueprints.yaml");
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        Map<Integer, BlueprintModel> bp = null;
        try {
            bp = om.readValue(file, new TypeReference<Map<Integer, BlueprintModel>>(){});
        } catch (IOException e) {
            throw new RuntimeException("Unable to read YML file: " + "D:/tmp/sde/fsd/blueprints.yaml", e);
        }
        return new ArrayList<>(bp.values());
    }
}
