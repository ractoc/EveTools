package com.ractoc.eve.assets.handler;

import com.ractoc.eve.assets.service.TypeService;
import com.speedment.runtime.core.component.transaction.Transaction;
import com.speedment.runtime.core.component.transaction.TransactionHandler;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TypeHandlerTest implements WithAssertions {

    @InjectMocks
    private TypeHandler handler;

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

}
