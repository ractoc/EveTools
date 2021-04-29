package com.ractoc.eve.crawler.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ractoc.eve.crawler.model.YamlTypeModel;
import org.springframework.batch.item.ItemReader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TypeItemReader implements ItemReader<YamlTypeModel> {

    private int nextTypeIndex = 0;
    private List<YamlTypeModel> types;

    public YamlTypeModel read() {
        if (types == null) {
            types = fetchTypesFromYML();
        }
        YamlTypeModel type = null;
        if (nextTypeIndex < types.size()) {
            type = types.get(nextTypeIndex);
            nextTypeIndex++;
        }
        return type;
    }

    private List<YamlTypeModel> fetchTypesFromYML() {
        try {
            URL file = this.getClass().getClassLoader().getResource("typeIDs.yaml");
            ObjectMapper om = new ObjectMapper(new YAMLFactory());
            Map<Integer, YamlTypeModel> bp = om.readValue(file, new TypeReference<Map<Integer, YamlTypeModel>>() {
            });
            bp.forEach((key, value) -> value.setId(key));
            return new ArrayList<>(bp.values());
        } catch (IOException e) {
            throw new SdeReaderException("Unable to read YML file: typeIDs.yaml", e);
        }
    }
}
