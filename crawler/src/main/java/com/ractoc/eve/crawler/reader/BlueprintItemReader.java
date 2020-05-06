package com.ractoc.eve.crawler.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ractoc.eve.crawler.model.YamlBlueprintModel;
import org.springframework.batch.item.ItemReader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlueprintItemReader implements ItemReader<YamlBlueprintModel> {

    private int nextBlueprintIndex = 0;
    private List<YamlBlueprintModel> blueprints;

    public YamlBlueprintModel read() {
        if (blueprints == null) {
            blueprints = fetchBlueprintsFromYML();
        }
        YamlBlueprintModel blueprint = null;
        if (nextBlueprintIndex < blueprints.size()) {
            blueprint = blueprints.get(nextBlueprintIndex);
            nextBlueprintIndex++;
        }
        return blueprint;
    }

    private List<YamlBlueprintModel> fetchBlueprintsFromYML() {
        try {
            URL file = this.getClass().getClassLoader().getResource("blueprints.yaml");
            ObjectMapper om = new ObjectMapper(new YAMLFactory());
            Map<Integer, YamlBlueprintModel> bp = om.readValue(file, new TypeReference<Map<Integer, YamlBlueprintModel>>() {
            });
            return new ArrayList<>(bp.values());
        } catch (IOException e) {
            throw new SdeReaderException("Unable to read YML file: blueprints.yaml", e);
        }
    }
}
