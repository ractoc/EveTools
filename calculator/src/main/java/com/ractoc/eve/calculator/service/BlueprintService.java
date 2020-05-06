package com.ractoc.eve.calculator.service;

import com.ractoc.eve.assets_client.ApiException;
import com.ractoc.eve.assets_client.api.BlueprintResourceApi;
import com.ractoc.eve.assets_client.model.BlueprintModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlueprintService {

    private BlueprintResourceApi blueprintResourceApi;

    @Autowired
    public BlueprintService(BlueprintResourceApi blueprintResourceApi) {
        this.blueprintResourceApi = blueprintResourceApi;
    }

    public BlueprintModel getBlueprint(Integer bpId) {
        int retryCount = 0;
        while (retryCount < 10) {
            try {
                return blueprintResourceApi.getBlueprint(bpId).getBlueprint();
            } catch (ApiException e) {
                if (e.getCode() != 502) {
                    throw new ServiceException("Unable to retrieve Blueprint: " + bpId, e);
                }
                retryCount++;
            }
        }
        throw new ServiceException("Unable to retrieve Blueprint: " + bpId);
    }
}
