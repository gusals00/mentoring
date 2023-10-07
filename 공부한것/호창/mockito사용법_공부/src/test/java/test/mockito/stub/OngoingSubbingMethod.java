package test.mockito.stub;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import test.mockito.mockito_test.Product;
import test.mockito.mockito_test.ProductService;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OngoingSubbingMethod {

    @Mock
    ProductService productService;

    @Test
    void testThenReturn() {
        Product product = new Product("T001", "mouse");
        when(productService.getProduct()).thenReturn(product);
        assertThat(productService.getProduct()).isEqualTo(product);
    }

    @Test
    void testThenThrows() {
        when(productService.getProduct()).thenThrow(new IllegalArgumentException());
        assertThatThrownBy(()->productService.getProduct()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testThenCallRealMethod() {
        when(productService.getProduct()).thenCallRealMethod();
        assertThat(productService.getProduct().getSerial()).isEqualTo("A001");
    }

    @Test
    void testConsecutiveStubbing() {
        Product product = new Product("D001", "water");
        when(productService.getProduct())
                .thenReturn(product)
                .thenThrow(new RuntimeException());
        assertThat(productService.getProduct()).isEqualTo(product);
        assertThatThrownBy(()->productService.getProduct()).isInstanceOf(RuntimeException.class);
    }
}
