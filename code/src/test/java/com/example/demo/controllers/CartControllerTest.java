package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.Assert.*;

import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CartControllerTest {
    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    CartRepository cartRepository;

    @InjectMocks
    CartController cartController;

    ModifyCartRequest modifyCartRequest;
    User user;
    Cart cart;
    Item item;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        item = new Item();
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(30));
        item.setName("Any product");
        item.setDescription("Any product description");

        cart = new Cart();
        cart.setId(1L);
        cart.addItem(item);

        user = new User();
        user.setUsername("Cris");
        user.setId(1L);
        user.setPassword("newPasswordForCris");
        user.setCart(cart);

        cart.setUser(user);

        modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(item.getId());
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setQuantity(1);
    }

    @Test
    public void happy_path_test_addToCart(){
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> cartResponseEntity = cartController.addTocart(modifyCartRequest);
        Cart cartSave = cartResponseEntity.getBody();

        assertNotNull(cartResponseEntity);
        assertEquals(200, cartResponseEntity.getStatusCodeValue());
        assertNotNull(cartSave);
        assertEquals(user, cartSave.getUser());
        assertEquals(BigDecimal.valueOf(60), cartSave.getTotal());
    }

    @Test
    public void happy_path_test_removeCart(){
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> cartResponseEntity = cartController.removeFromcart(modifyCartRequest);
        Cart cartRemoveItems = cartResponseEntity.getBody();

        assertNotNull(cartResponseEntity);
        assertEquals(200, cartResponseEntity.getStatusCodeValue());
        assertNotNull(cartRemoveItems);
        assertEquals(0, cartRemoveItems.getItems().size());
        assertEquals(user, cartRemoveItems.getUser());
    }

    @Test
    public void happy_path_test_item_not_found(){
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        modifyCartRequest.setItemId(5L);

        final ResponseEntity<Cart> cartResponseEntity = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(cartResponseEntity);
        assertEquals(404, cartResponseEntity.getStatusCodeValue());
    }
}
