package com.ractoc.eve.crawler.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ractoc.eve.domain.assets.BlueprintModel;
import org.springframework.batch.item.ItemReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlueprintItemReader implements ItemReader<BlueprintModel> {

    private int nextBlueprintIndex = 0;
    private List<BlueprintModel> blueprints;

    public BlueprintModel read() {
        if (blueprints == null) {
            blueprints = fetchBlueprintsFromYML();
        }
        BlueprintModel blueprint = null;
        if (nextBlueprintIndex < blueprints.size()) {
            blueprint = blueprints.get(nextBlueprintIndex);
            nextBlueprintIndex++;
        }
        return blueprint;
    }

    private List<BlueprintModel> fetchBlueprintsFromYML() {
        try {
            // FIXME: retreive the filename from a commandline param
            File file = new File("D:/tmp/sde/fsd/blueprints.yaml");
            ObjectMapper om = new ObjectMapper(new YAMLFactory());
            Map<Integer, BlueprintModel> bp = om.readValue(file, new TypeReference<Map<Integer, BlueprintModel>>() {
            });
            return new ArrayList<>(bp.values());
        } catch (IOException e) {
            throw new SdeReaderException("Unable to read YML file: " + "D:/tmp/sde/fsd/blueprints.yaml", e);
        }
    }
}
