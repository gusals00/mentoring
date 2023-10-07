package test.mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.AtLeast;
import org.mockito.junit.jupiter.MockitoExtension;
import test.mockito.mockito_test.UserService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VerifyMethod {

    @Mock
    UserService userService;

    @Test
    void testVerifyTimes() {
        userService.getUser();
        userService.getUser();

        verify(userService, times(2)).getUser();
    }

    @Test
    void testVerifyNever() {
        verify(userService,never()).getUser();
    }

    @Test
    void testAtLeastOne() {
        userService.getUser();
        verify(userService, atLeastOnce()).getUser();
    }

    @Test
    void testAtLeast() {
        userService.getUser();
        userService.getUser();
        userService.getUser();

        verify(userService,atLeast(2)).getUser();
    }

    @Test
    void testAtMostOnce() {
        userService.getUser();
        verify(userService, atMostOnce()).getUser();
    }

    @Test
    void testAtMost() {
        userService.getUser();
        userService.getUser();
        userService.getUser();

        verify(userService, atMost(3)).getUser();
    }

    @Test
    void testOnly() {
        userService.getUser();
//        userService.getLoginErrNum();

        verify(userService,only()).getUser();
    }


    @Test
    void testDoNothing() {
        doNothing().when(userService).deleteUser();
        userService.deleteUser();
        verify(userService,only()).deleteUser();
    }
}
