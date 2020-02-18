package com.ractoc.eve.crawler.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ractoc.eve.domain.universe.MarketHubModel;
import org.springframework.batch.item.ItemReader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MarketHubItemReader implements ItemReader<MarketHubModel> {

    private int nextMarketHubIndex = 0;
    private List<MarketHubModel> marketHubs;

    public MarketHubModel read() {
        if (marketHubsNotInitialized()) {
            marketHubs = fetchMarketHubsFromYML();
        }

        MarketHubModel marketHub = null;

        if (nextMarketHubIndex < marketHubs.size()) {
            marketHub = marketHubs.get(nextMarketHubIndex);
            nextMarketHubIndex++;
        }

        return marketHub;
    }

    private boolean marketHubsNotInitialized() {
        return this.marketHubs == null;
    }

    private List<MarketHubModel> fetchMarketHubsFromYML() {
        try {
            URL file = this.getClass().getClassLoader().getResource("marketHubs.yaml");
            ObjectMapper om = new ObjectMapper(new YAMLFactory());
            Map<Integer, MarketHubModel> bp = om.readValue(file, new TypeReference<Map<Integer, MarketHubModel>>() {
            });
            bp.values().removeIf(Objects::isNull);
            bp.forEach((key, value) -> value.setId(key));
            return new ArrayList<>(bp.values());
        } catch (IOException e) {
            throw new SdeReaderException("Unable to read YML file: " + "marketHubs.yaml", e);
        }
    }
}
