package com.samnang.ecommerce.service.impl;

import com.samnang.ecommerce.exceptions.ApiException;
import com.samnang.ecommerce.exceptions.ResourceNotFoundException;
import com.samnang.ecommerce.models.Cart;
import com.samnang.ecommerce.models.CartItem;
import com.samnang.ecommerce.models.Product;
import com.samnang.ecommerce.payload.CartDTO;
import com.samnang.ecommerce.payload.CartItemDTO;
import com.samnang.ecommerce.payload.ProductDTO;
import com.samnang.ecommerce.repositories.CartItemRepository;
import com.samnang.ecommerce.repositories.CartRepository;
import com.samnang.ecommerce.repositories.ProductRepository;
import com.samnang.ecommerce.service.CartService;
import com.samnang.ecommerce.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private  CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        // Find existing cart or create one
        Cart cart = createCart();

        // Retrieve Product Details
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getId(),productId);

        // Perform Validation
        if (cartItem != null) {
            throw new ApiException("Product" + product.getProductName() + "already exists");
        }
        if (product.getQuantity() == 0){
            throw new ApiException(product.getProductName() + "is not available");
        }
        if (product.getQuantity() < quantity){
            throw new ApiException("Please, make an order of the " + product.getProductName() + "!"
                + " You need " + quantity + " of " + product.getQuantity() + "!");
        }

        // Create Cart Item
        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        // Save Cart Item
        cartItemRepository.save(newCartItem);

        // Update Cart
        product.setQuantity(product.getQuantity() - quantity);
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());

        // Return Updated
        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if(carts.isEmpty()){
            throw new ApiException("No carts found");
        }
        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream().map(cartItem -> {
                ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                productDTO.setQuantity(cartItem.getQuantity()); // Set the quantity from CartItem
                return productDTO;
            }).collect(Collectors.toList());

            cartDTO.setProducts(products);

            return cartDTO;

        }).toList();

        return cartDTOs;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }
        CartDTO cartDTO = modelMapper.map( cart, CartDTO.class);
        cart.getCartItems().forEach(c ->
                c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(p -> modelMapper
                        .map(p.getProduct(), ProductDTO.class)).toList();
        cartDTO.setProducts(products);

        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId  = userCart.getId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if (product.getQuantity() == 0) {
            throw new ApiException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new ApiException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new ApiException("Product " + product.getProductName() + " not available in the cart!!!");
        }
        int newQuantity = cartItem.getQuantity() + quantity;

        if (newQuantity < 0) {
            throw new ApiException("The resulting quantity can't be less than zero !");
        }

        if (newQuantity == 0){
            deleteProductFromCart(cartId, productId);
        } else {

            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
            CartItem updatedItem = cartItemRepository.save(cartItem);
            if (updatedItem.getQuantity() == 0) {
                cartItemRepository.deleteById(updatedItem.getCartItemId());
            }
        }


        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO prd = modelMapper.map(item.getProduct(), ProductDTO.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        });


        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice() -
                (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart !!!";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new ApiException("Product " + product.getProductName() + " not available in the cart!!!!");
        }

        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItem = cartItemRepository.save(cartItem);
    }

    @Override
    public String createOrUpdateCartWithItem(List<CartItemDTO> cartItems) {
        String emailId = authUtil.loggedInEmail();

        Cart existingCart = cartRepository.findCartByEmail(emailId);

        if (existingCart == null) {
             existingCart = new Cart();
             existingCart.setTotalPrice(0.00);
             existingCart.setUser(authUtil.loggedInUser());
             cartRepository.save(existingCart);
        }else {
            // if cart represent delete all cart exist
            cartItemRepository.deleteAllByCartId(existingCart.getId());

            double totalPrice = 0.0;
            for (CartItemDTO cartItemDTO : cartItems) {
                Long productId = cartItemDTO.getProductId();

                Integer quantity = cartItemDTO.getQuantity();

                //Find product by id
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

              //  product.setQuantity(product.getQuantity() - quantity);
                productRepository.save(product);

                totalPrice += product.getSpecialPrice()  * quantity;

                CartItem cartItem = new CartItem();
                    cartItem.setProduct(product);
                    cartItem.setCart(existingCart);
                    cartItem.setProductPrice(product.getSpecialPrice());
                    cartItem.setQuantity(quantity);
                    cartItem.setDiscount(product.getDiscount());
                    cartItemRepository.save(cartItem);
            }
            existingCart.setTotalPrice(totalPrice);
            existingCart.setUser(authUtil.loggedInUser());
            cartRepository.save(existingCart);

        }
        return "Cart added successfully!!!";
    }

    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail((authUtil.loggedInEmail()));
        if (userCart != null) {
            return userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());

        return cartRepository.save(cart);
    }


}
