package com.ractoc.eve.crawler.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ractoc.eve.domain.assets.TypeModel;
import org.springframework.batch.item.ItemReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TypeItemReader implements ItemReader<TypeModel> {

    private int nextTypeIndex = 0;
    private List<TypeModel> types;

    public TypeModel read() {
        if (types == null) {
            types = fetchTypesFromYML();
        }
        TypeModel type = null;
        if (nextTypeIndex < types.size()) {
            type = types.get(nextTypeIndex);
            nextTypeIndex++;
        }
        System.out.println("read type:");
        System.out.println(type);
        return type;
    }

    private List<TypeModel> fetchTypesFromYML() {
        try {
            // FIXME: retreive the folder from a commandline param
            File file = new File("D:/tmp/sde/fsd/typeIDs.yaml");
            ObjectMapper om = new ObjectMapper(new YAMLFactory());
            Map<Integer, TypeModel> bp = om.readValue(file, new TypeReference<Map<Integer, TypeModel>>() {
            });
            bp.forEach((key, value) -> value.setId(key));
            return new ArrayList<>(bp.values());
        } catch (IOException e) {
            throw new SdeReaderException("Unable to read YML file: " + "D:/tmp/sde/fsd/typeIDs.yaml", e);
        }
    }
}
