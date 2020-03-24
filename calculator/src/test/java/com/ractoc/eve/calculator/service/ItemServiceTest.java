package com.ractoc.eve.calculator.service;

import com.ractoc.eve.assets_client.ApiException;
import com.ractoc.eve.assets_client.api.ItemResourceApi;
import com.ractoc.eve.assets_client.model.ItemModel;
import com.ractoc.eve.assets_client.model.ItemResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest implements WithAssertions {

    @InjectMocks
    private ItemService service;

    @Mock
    private ItemResourceApi mockedItemResourceApi;

    @Test
    void getItemForBlueprint() throws ApiException {
        // Given
        ItemModel item = createItemModel();
        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setItem(item);
        when(mockedItemResourceApi.getItemForBlueprint(25)).thenReturn(itemResponse);

        // When
        ItemModel result = service.getItemForBlueprint(25);

        // Then
        assertThat(result).isNotNull().extracting("id").isEqualTo(100);
    }

    @Test
    void getItemForBlueprintException() throws ApiException {
        // Given
        when(mockedItemResourceApi.getItemForBlueprint(25)).thenThrow(new ApiException("test exception"));

        // When
        Throwable thrown = catchThrowable(() -> service.getItemForBlueprint(25));

        // Then
        assertThat(thrown)
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("Unable to retrieve Item for Blueprint: 25");
    }

    private ItemModel createItemModel() {
        ItemModel item = new ItemModel();
        item.setId(100);
        item.setName("random item");
        return item;
    }
}
