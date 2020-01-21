package com.ractoc.eve.crawler.reader;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ractoc.eve.domain.assets.TypeModel;

import java.util.Map;

public class TypeMap {

    Map<Integer, TypeModel> mp;

    @JsonCreator
    public TypeMap(Map<Integer, TypeModel> mp) {
        this.mp = mp;
    }

}
