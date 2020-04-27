package com.ractoc.eve.crawler.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ractoc.eve.crawler.model.YamlMarketGroupModel;
import org.springframework.batch.item.ItemReader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarketGroupItemReader implements ItemReader<YamlMarketGroupModel> {

    private int nextMarketGroupIndex = 0;
    private List<YamlMarketGroupModel> marketGroups;

    public YamlMarketGroupModel read() {
        if (marketGroups == null) {
            marketGroups = fetchMarketGroupsFromYML();
        }
        YamlMarketGroupModel marketGroup = null;
        if (nextMarketGroupIndex < marketGroups.size()) {
            marketGroup = marketGroups.get(nextMarketGroupIndex);
            nextMarketGroupIndex++;
        }
        return marketGroup;
    }

    private List<YamlMarketGroupModel> fetchMarketGroupsFromYML() {
        try {
            URL file = this.getClass().getClassLoader().getResource("marketGroups.yaml");
            ObjectMapper om = new ObjectMapper(new YAMLFactory());
            Map<Integer, YamlMarketGroupModel> bp = om.readValue(file, new TypeReference<>() {
            });
            bp.forEach((key, value) -> {
                value.setId(key);
            });
            return new ArrayList<>(bp.values());
        } catch (IOException e) {
            throw new SdeReaderException("Unable to read YML file: marketGroupIDs.yaml", e);
        }
    }
}
