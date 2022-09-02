package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.Assert.*;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ItemController itemController;

    List<Item> items;
    Item itemOne;
    Item itemTwo;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        itemOne = new Item();
        itemOne.setId(1L);
        itemOne.setName("Macbook");
        itemOne.setDescription("Macbook Pro 17 new");
        itemOne.setPrice(BigDecimal.valueOf(5000));

        itemTwo = new Item();
        itemTwo.setId(2L);
        itemTwo.setName("Bike");
        itemTwo.setDescription("Bike downhill");
        itemTwo.setPrice(BigDecimal.valueOf(1000));

        items = new LinkedList<>();
        items.add(itemOne);
        items.add(itemTwo);
    }

    @Test
    public void happy_path_test_getItems(){
        when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> itemsResponseEntity = itemController.getItems();
        List<Item> itemList = itemsResponseEntity.getBody();
        assertNotNull(itemsResponseEntity);
        assertEquals(200, itemsResponseEntity.getStatusCodeValue());
        assertNotNull(itemList);
        assertArrayEquals(items.toArray(), itemList.toArray());
    }

    @Test
    public void happy_path_test_getItemById(){
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemOne));
        ResponseEntity<Item> itemsResponseEntity = itemController.getItemById(1L);
        Item itemSaved = itemsResponseEntity.getBody();
        assertNotNull(itemsResponseEntity);
        assertEquals(200, itemsResponseEntity.getStatusCodeValue());

        assertNotNull(itemSaved);
        assertEquals("Macbook Pro 17 new", itemSaved.getDescription());
        assertEquals(BigDecimal.valueOf(5000), itemSaved.getPrice());
    }

    @Test
    public void happy_path_test_getItemsByName(){
        when(itemRepository.findByName(anyString())).thenReturn(items);

        ResponseEntity<List<Item>> itemsResponseEntity = itemController.getItemsByName("Any Item");
        List<Item> itemList = itemsResponseEntity.getBody();
        assertNotNull(itemsResponseEntity);
        assertEquals(200, itemsResponseEntity.getStatusCodeValue());
        assertNotNull(itemList);
        assertArrayEquals(items.toArray(), itemList.toArray());
    }
}
