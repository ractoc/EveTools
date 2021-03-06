package com.ractoc.eve.assets.handler;

import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_products.BlueprintManufacturingProducts;
import com.ractoc.eve.assets.mapper.ItemMapper;
import com.ractoc.eve.assets.mapper.TypeMapper;
import com.ractoc.eve.assets.service.BlueprintService;
import com.ractoc.eve.assets.service.TypeService;
import com.ractoc.eve.domain.assets.ItemModel;
import com.ractoc.eve.domain.assets.TypeModel;
import com.speedment.runtime.core.component.transaction.TransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TypeHandler {

    private final BlueprintService blueprintService;
    private final TypeService typeService;
    private final TransactionHandler transactionHandler;

    @Autowired
    public TypeHandler(TransactionHandler transactionHandler,
                       BlueprintService blueprintService,
                       TypeService typeService) {
        this.typeService = typeService;
        this.blueprintService = blueprintService;
        this.transactionHandler = transactionHandler;
    }

    public void saveTypes(List<TypeModel> types) {
        transactionHandler.createAndAccept(tx -> {
            typeService.clearAllTypes();
            types.stream()
                    .map(TypeMapper.INSTANCE::modelToDb)
                    .forEach(typeService::saveType);
            tx.commit();
        });
    }

    public String getItemName(int itemId) {
        return typeService.getItemName(itemId);
    }

    public ItemModel getItemById(Integer id) {
        return ItemMapper.INSTANCE.dbToModel(typeService.getTypeById(id));
    }

    public ItemModel getItemForBlueprint(int blueprintId) {
        Set<BlueprintManufacturingProducts> products = blueprintService.getManufacturingProducts(blueprintId);
        Integer itemId = products.stream()
                .findAny()
                .map(BlueprintManufacturingProducts::getTypeId)
                .orElseThrow(() -> new NoSuchElementException("No items found for blueprint, ID: " + blueprintId));
        return ItemMapper.INSTANCE.dbToModel(typeService.getTypeById(itemId));
    }

    public List<ItemModel> getItemsByName(@NotEmpty String name) {
        return typeService.getItemsByName(name).map(ItemMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }

    public List<ItemModel> getItemsByMarketGroup(@NotNull Integer group) {
        return typeService.getItemsByMarketGroup(group).map(ItemMapper.INSTANCE::dbToModel).collect(Collectors.toList());
    }
}
