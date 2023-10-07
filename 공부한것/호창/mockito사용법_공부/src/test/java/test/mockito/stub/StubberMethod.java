package test.mockito.stub;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import test.mockito.mockito_test.User;
import test.mockito.mockito_test.UserService;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StubberMethod {

    @Mock
    UserService userService;

    @Test
    void testDoReturn() {
        User user = new User("badguy", "1234");
        doReturn(user).when(userService).getUser();
        assertThat(userService.getUser()).isEqualTo(user);
    }

    @Test
    void testDoThrow() {
        doThrow(new RuntimeException()).when(userService).deleteUser();
        assertThatThrownBy(()->userService.deleteUser()).isInstanceOf(RuntimeException.class);
    }

}
