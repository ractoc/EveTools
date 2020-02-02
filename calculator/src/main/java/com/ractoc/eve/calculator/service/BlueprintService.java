package com.ractoc.eve.calculator.service;

import com.ractoc.eve.assets_client.ApiException;
import com.ractoc.eve.assets_client.api.BlueprintResourceApi;
import com.ractoc.eve.assets_client.model.BlueprintModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlueprintService {

    @Autowired
    private BlueprintResourceApi blueprintResourceApi;

    public BlueprintModel getBlueprint(Integer bpId) {
        try {
            return blueprintResourceApi.getBlueprint(bpId).getBlueprint();
        } catch (ApiException e) {
            throw new ServiceException("Unable to retrieve Blueprint: " + bpId, e);
        }
    }
}
