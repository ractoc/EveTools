package com.ractoc.eve.assets.handler;

import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_products.BlueprintManufacturingProducts;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_products.BlueprintManufacturingProductsImpl;
import com.ractoc.eve.assets.db.assets.eve_assets.type.Type;
import com.ractoc.eve.assets.db.assets.eve_assets.type.TypeImpl;
import com.ractoc.eve.assets.service.BlueprintService;
import com.ractoc.eve.assets.service.TypeService;
import com.ractoc.eve.domain.assets.ItemModel;
import com.ractoc.eve.domain.assets.TypeModel;
import com.speedment.runtime.core.component.transaction.Transaction;
import com.speedment.runtime.core.component.transaction.TransactionHandler;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TypeHandlerTest implements WithAssertions {

    @InjectMocks
    private TypeHandler handler;

    @Captor
    private ArgumentCaptor<Consumer<? super Transaction>> saveTypesLambdaCaptor;
    @Captor
    private ArgumentCaptor<Type> typeCaptor;

    @Mock
    private BlueprintService mockedBlueprintService;
    @Mock
    private TypeService mockedTypeService;
    @Mock
    private TransactionHandler mockedTransactionHandler;
    @Mock
    private Transaction mockedTransaction;

    @Test
    void getItemName() {
        // Given
        when(mockedTypeService.getItemName(50)).thenReturn("test name");

        // When
        String name = handler.getItemName(50);

        // Then
        assertThat(name).isNotNull().isEqualTo("test name");
    }

    @Test
    void getItemForBlueprint() {
        // Given
        Set<BlueprintManufacturingProducts> manProds = createBpManProds();
        when(mockedBlueprintService.getManufacturingProducts(25)).thenReturn(manProds);
        when(mockedTypeService.getItemById(20)).thenReturn(createType());

        // When
        ItemModel model = handler.getItemForBlueprint(25);

        // Then
        assertThat(model).isNotNull().extracting("name").isEqualTo("test type");
    }

    @Test
    void getItemForBlueprintNotFound() {
        // Given
        when(mockedBlueprintService.getManufacturingProducts(25)).thenReturn(new HashSet<>());

        // When
        Throwable thrown = catchThrowable(() -> handler.getItemForBlueprint(25));

        // Then
        assertThat(thrown)
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("No items found for blueprint, ID: 25");
    }

    private Type createType() {
        Type t = new TypeImpl();
        t.setId(15);
        t.setName("test type");
        return t;
    }

    private Set<BlueprintManufacturingProducts> createBpManProds() {
        Set<BlueprintManufacturingProducts> manProds = new HashSet<>();
        BlueprintManufacturingProducts manProd = new BlueprintManufacturingProductsImpl();
        manProd.setTypeId(20);
        manProds.add(manProd);
        return manProds;
    }

    @Test
    void saveTypes() {
        // Given
        TypeModel typeModel = createTypeModel();

        // When
        handler.saveTypes(Stream.of(typeModel).collect(Collectors.toList()));

        // Then
        verify(mockedTransactionHandler).createAndAccept(saveTypesLambdaCaptor.capture());
        Consumer<? super Transaction> lambda = saveTypesLambdaCaptor.getValue();
        lambda.accept(mockedTransaction);
        verify(mockedTypeService).clearAllTypes();
        verify(mockedTypeService).saveType(typeCaptor.capture());
        Type t = typeCaptor.getValue();
        assertThat(t.getId()).isEqualTo(25);
        assertThat(t.getName()).isEqualTo("test type");
        verify(mockedTransaction).commit();
    }

    private TypeModel createTypeModel() {
        return TypeModel.builder().id(25).name("test type").build();
    }
}
