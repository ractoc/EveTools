package com.ractoc.eve.crawler.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ractoc.eve.assets.handler.TypeHandler;
import com.ractoc.eve.domain.assets.TypeModel;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TypeItemReader implements ItemReader<TypeModel> {

    private int nextTypeIndex = 0;
    private List<TypeModel> types;

    @Autowired
    private TypeHandler handler;

    public TypeModel read() {
        if (types == null) {
            init();
        }
        TypeModel type = null;
        if (nextTypeIndex < types.size()) {
            type = types.get(nextTypeIndex);
            nextTypeIndex++;
        }
        return type;
    }

    private void init() {
        types = fetchTypesFromYML();
        handler.clearAllTypes();
    }

    private List<TypeModel> fetchTypesFromYML() {
        try {
            // FIXME: retreive the filename from a commandline param
            File file = new File("D:/tmp/sde/fsd/typeIDs.yaml");
            ObjectMapper om = new ObjectMapper(new YAMLFactory());
            Map<Integer, TypeModel> bp = om.readValue(file, new TypeReference<Map<Integer, TypeModel>>() {
            });
            bp.values().removeIf(Objects::isNull);
            bp.forEach((key, value) -> value.setId(key));
            return new ArrayList<>(bp.values());
        } catch (IOException e) {
            throw new SdeReaderException("Unable to read YML file: " + "D:/tmp/sde/fsd/typeIDs.yaml", e);
        }
    }
}
