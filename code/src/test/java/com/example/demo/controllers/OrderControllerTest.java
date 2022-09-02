package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OrderControllerTest {
    @Mock
    UserRepository userRepository;

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderController orderController;

    private User user;
    private Cart cart;
    Item itemOne;
    Item itemTwo;
    List<Item> itemList;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        user = new User();
        user.setUsername("Cris");
        cart = new Cart();
        cart.setUser(user);

        itemOne = new Item();
        itemOne.setId(1L);
        itemOne.setPrice(BigDecimal.valueOf(20));
        itemOne.setName("Any product");
        itemOne.setDescription("Any product description");
        cart.addItem(itemOne);

        itemTwo = new Item();
        itemTwo.setId(1L);
        itemTwo.setPrice(BigDecimal.valueOf(30));
        itemTwo.setName("Any product Two");
        itemTwo.setDescription("Any product description Two");
        cart.addItem(itemTwo);

        user.setCart(cart);

        itemList = new LinkedList<>();
        itemList.add(itemOne);
        itemList.add(itemTwo);
    }

    // Helper method to create orders and call it in test functions
    private List<UserOrder> createHistoryOrdersForUser(User user){
        UserOrder order1 = new UserOrder();
        order1.setId(1L);
        order1.setItems(itemList);
        order1.setUser(user);
        order1.setTotal(new BigDecimal(20 + 30));

        UserOrder order2 = new UserOrder();
        order1.setId(2L);
        order1.setItems(itemList);
        order1.setUser(user);
        order1.setTotal(new BigDecimal(20 + 30));

        List<UserOrder> userOrders = new ArrayList<>();
        userOrders.add(order1);
        userOrders.add(order2);

        return userOrders;
    }
    @Test
    public void happy_path_test_find_orders_by_user(){
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        List<UserOrder> userOrders = createHistoryOrdersForUser(user);
        when(orderRepository.findByUser(user)).thenReturn(userOrders);

        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("Cris");
        List<UserOrder> userOrdersTestings = responseEntity.getBody();

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        assertNotNull(userOrdersTestings);
        assertArrayEquals(userOrders.toArray(), userOrdersTestings.toArray());

        verify(userRepository, times(1)).findByUsername("Cris");
        verify(orderRepository, times(1)).findByUser(user);
    }

    @Test
    public void happy_path_test_submit_order(){
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        ResponseEntity<UserOrder> userOrderResponseEntity = orderController.submit("Cris");
        UserOrder userOrderTest = userOrderResponseEntity.getBody();

        assertNotNull(userOrderResponseEntity);
        assertEquals(200, userOrderResponseEntity.getStatusCodeValue());

        assertNotNull(userOrderTest);
        assertEquals(2, userOrderTest.getItems().size());
        assertEquals(user, userOrderTest.getUser());
        assertEquals(BigDecimal.valueOf(50), userOrderTest.getTotal());

        verify(userRepository).findByUsername("Cris");
        verify(orderRepository).save(any(UserOrder.class));

    }
}
