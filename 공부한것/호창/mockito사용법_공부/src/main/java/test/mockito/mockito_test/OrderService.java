package test.mockito.mockito_test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderService {

    private UserService userService;
    private ProductService productService;


    public User getUser() {
        return userService.getUser();
    }

    public Product getProduct() {
        return productService.getProduct();
    }
}
