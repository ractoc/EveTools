package com.ractoc.eve.calculator.service;

import com.ractoc.eve.assets_client.ApiException;
import com.ractoc.eve.assets_client.api.ItemResourceApi;
import com.ractoc.eve.assets_client.model.ItemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    @Autowired
    private ItemResourceApi itemResourceApi;

    public ItemModel getItemForBlueprint(Integer blueprintId) {
        try {
            return itemResourceApi.getItemForBlueprint(blueprintId).getItem();
        } catch (ApiException e) {
            throw new ServiceException("Unable to retrieve Item for Blueprint: " + blueprintId, e);
        }
    }
}
