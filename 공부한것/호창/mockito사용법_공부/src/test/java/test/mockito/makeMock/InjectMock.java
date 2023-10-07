package test.mockito.makeMock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import test.mockito.mockito_test.OrderService;
import test.mockito.mockito_test.Product;
import test.mockito.mockito_test.ProductService;
import test.mockito.mockito_test.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class InjectMock {
    @Mock
    UserService userService;
    @Spy
    ProductService productService;

    @InjectMocks
    OrderService orderService;

    @Test
    void testGetUser() {
        assertNull(orderService.getUser());
    }

    @Test
    void testGetProduct() {
        Product product = orderService.getProduct();
        assertEquals("A001",product.getSerial());
    }
}
