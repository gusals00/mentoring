package test.mockito.mockito_test;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserService {
    public User getUser() {
        return new User("effort", "1234");
    }

    public int getLoginErrNum() {
        return 1;
    }

    public void deleteUser() {
    }
}
