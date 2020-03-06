package com.ractoc.eve.crawler.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ractoc.eve.crawler.model.deserializers.TypeDeserializer;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonDeserialize(using = TypeDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class YamlTypeModel {
    private int id;
    private String name;
    private int groupId;
    private boolean published;

    public boolean isComplete() {
        return id != 0 && name != null && groupId != 0;
    }
}
