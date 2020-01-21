package com.ractoc.eve.assets.handler;

import com.ractoc.eve.assets.mapper.TypeMapper;
import com.ractoc.eve.assets.service.TypeService;
import com.ractoc.eve.domain.assets.TypeModel;
import com.speedment.runtime.core.component.transaction.TransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TypeHandler {

    private TypeService typeService;
    private TransactionHandler transactionHandler;

    @Autowired
    public TypeHandler(TransactionHandler transactionHandler,
                       TypeService typeService) {
        this.typeService = typeService;
        this.transactionHandler = transactionHandler;
    }

    public void saveTypes(List<? extends TypeModel> bps) {
        transactionHandler.createAndAccept(tx -> {
            bps.stream()
                    .map(TypeMapper.INSTANCE::modelToDb)
                    .forEach(typeService::saveType);
            tx.commit();
        });
    }

    public void clearAllTypes() {
        transactionHandler.createAndAccept(tx -> {
            typeService.clearAllTypes();
            tx.commit();
        });
    }
}
