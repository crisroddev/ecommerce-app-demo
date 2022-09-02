package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    @Mock
    UserRepository userRepository;

    @Mock
    CartRepository cartRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    UserController userController;

    MockMvc mockMvc;

    CreateUserRequest createUserRequest;
    ResponseEntity<User> userResponseEntity;
    User userSaved;

    @Before
    public void setUp(){
        userController = new UserController(userRepository, cartRepository, bCryptPasswordEncoder);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("happyTest");
        createUserRequest.setPassword("happyPassword");
        createUserRequest.setConfirmPassword("happyPassword");
    }

    @Test
    public void happy_path_test_create_user() {
        when(bCryptPasswordEncoder.encode("happyPassword")).thenReturn("PasswordHashed");
        userResponseEntity = userController.createUser(createUserRequest);
        userSaved = userResponseEntity.getBody();
        assertNotNull(userResponseEntity);
        assertNotNull(userSaved);
        assertEquals(200, userResponseEntity.getStatusCodeValue());
        assertEquals(0, userSaved.getId());
        assertEquals("happyTest", userSaved.getUsername());
        assertEquals("PasswordHashed", userSaved.getPassword());
    }

    @Test
    public void  happy_path_test_find_user_by_id() throws Exception{
        userSaved = new User();
        userSaved.setId(1);

        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(userSaved));
        userResponseEntity = userController.findById(1L);
        assertNotNull(userResponseEntity);
        mockMvc.perform(get("/api/user/id/20"))
                .andExpect(status().isOk());
    }

    @Test
    public void  happy_path_test_find_user_by_username()throws Exception{
        userSaved = new User();
        userSaved.setUsername("testUsername");
        when(userRepository.findByUsername(anyString())).thenReturn(userSaved);
        userResponseEntity = userController.findByUserName("testUsername");
        assertNotNull(userResponseEntity);
        mockMvc.perform(get("/api/user/testUsername"))
                .andExpect(status().isOk());
    }
}
