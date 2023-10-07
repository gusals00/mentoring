package test.mockito.makeMock;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import test.mockito.mockito_test.Product;
import test.mockito.mockito_test.ProductService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpyAnnotation {

    @Spy
    ProductService productService;

    @Test
    void notStubbingSpy(){
        Product product = productService.getProduct();
        Assertions.assertThat(product.getSerial()).isEqualTo("A001");
    }

    @Test
    void stubbingSpy(){
        Product productDummy = new Product("B001", "keyboard");
        when(productService.getProduct()).thenReturn(productDummy);

        Product product = productService.getProduct();
        assertEquals(product.getSerial(),productDummy.getSerial());
    }
}
