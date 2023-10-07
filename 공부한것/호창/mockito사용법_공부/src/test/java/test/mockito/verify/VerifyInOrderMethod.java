package test.mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import test.mockito.mockito_test.ProductService;
import test.mockito.mockito_test.UserService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VerifyInOrderMethod {

    @Mock
    UserService userService;

    @Mock
    ProductService productService;

    @Test
    void testInOrder() {
        userService.getUser();
        userService.getLoginErrNum();

        InOrder inOrder = inOrder(userService);

        inOrder.verify(userService).getUser();
        inOrder.verify(userService).getLoginErrNum();
    }

    @Test
    void testInOrderWithCalls() {
        userService.getLoginErrNum();
        userService.getLoginErrNum();
        userService.getUser();
        userService.getLoginErrNum();

        InOrder inOrder = inOrder(userService);

        inOrder.verify(userService,calls(2)).getLoginErrNum();
        inOrder.verify(userService).getUser();
        inOrder.verify(userService,calls(1)).getLoginErrNum();
    }

    @Test
    void testInOrderWithVerifyNoMoreInteractions() {
        userService.getUser();

        InOrder inOrder = inOrder(userService);
        inOrder.verify(userService).getUser();

        verifyNoMoreInteractions(userService);
    }

    @Test
    void testInOrderVerifyNoInteractions() {
        userService.getUser();
        userService.getLoginErrNum();

        InOrder inOrder = inOrder(userService);

        inOrder.verify(userService).getUser();
        inOrder.verify(userService).getLoginErrNum();

        verifyNoInteractions(productService);
    }
}
