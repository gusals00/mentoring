package test.mockito.makeMock;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import test.mockito.mockito_test.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class MockAnnotation {

    @Mock
    UserService userService;

    @Test
    void testReferenceType() {
        assertNull(userService.getUser());
    }

    @Test
    void testPrimitiveType() {
        assertEquals(0,userService.getLoginErrNum());
    }

}
