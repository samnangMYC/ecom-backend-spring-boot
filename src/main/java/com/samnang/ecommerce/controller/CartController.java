package com.samnang.ecommerce.controller;

import com.samnang.ecommerce.models.Cart;
import com.samnang.ecommerce.payload.CartDTO;
import com.samnang.ecommerce.payload.CartItemDTO;
import com.samnang.ecommerce.repositories.CartRepository;
import com.samnang.ecommerce.service.CartService;
import com.samnang.ecommerce.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private CartRepository cartRepository;


    @PostMapping("/cart/create")
    public ResponseEntity<String> createOrUpdateCart(@RequestBody List<CartItemDTO> cartItems) {
        String response = cartService.createOrUpdateCartWithItems(cartItems);
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }


    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,
                                                    @PathVariable Integer quantity) {
        CartDTO cartDTO = cartService.addProductToCart(productId,quantity);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        List<CartDTO> cartDTOS = cartService.getAllCarts();

        return new ResponseEntity<List<CartDTO>>(cartDTOS, HttpStatus.OK);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById() {
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getId();
        CartDTO cartDTO = cartService.getCart(emailId, cartId);

        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(
            @PathVariable Long productId,
            @PathVariable String operation
    ){
        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId,
                operation.equalsIgnoreCase("delete") ? -1 : 1);

        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,
                                                        @PathVariable Long productId) {
        String status = cartService.deleteProductFromCart(cartId, productId);

        return new ResponseEntity<String>(status, HttpStatus.OK);
    }
}
