package com.ractoc.eve.domain.assets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ractoc.eve.domain.deserializers.TypeDeserializer;
import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@ApiModel(value = "Type Model", description = "Contains the Type model information")
@JsonDeserialize(using = TypeDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TypeModel {
    private int id;
    private String name;
    private int groupId;
    private int volume;
}
