package com.assegd.app.ws.controller;

import com.assegd.app.ws.service.UserService;
import com.assegd.app.ws.shared.dto.AddressDTO;
import com.assegd.app.ws.shared.dto.UserDto;
import com.assegd.app.ws.ui.model.response.UserRest;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    UserService userService;

    UserDto userDto;

    final String USER_ID = "klfeoivlsdk45lkjvdfdfdfd";

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        userDto = new UserDto();
        userDto.setFirstName("Assegd Assefa");
        userDto.setLastName("Asfaw");
        userDto.setEmail("assetozen@gmail.com");
        userDto.setEmailVerificationStatus(Boolean.FALSE);
        userDto.setEmailVerificationToken(null);
        userDto.setUserId(USER_ID);
        userDto.setAddresses((getAddressesDto()));
        userDto.setEncryptedPassword("xkljdaseviolk45");
    }

    @Test
    final void testGetUser() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);

        UserRest userRest = userController.getUser(USER_ID);

        assertNotNull(userRest);
        assertEquals(USER_ID, userRest.getUserId());
        assertEquals(userDto.getFirstName(), userRest.getFirstName());
        assertEquals(userDto.getLastName(), userRest.getLastName());
        assertTrue(userDto.getAddresses().size() == userRest.getAddresses().size());

    }


    private List<AddressDTO> getAddressesDto() {
        AddressDTO shippingAddressDTO = new AddressDTO();
        shippingAddressDTO.setType("shipping");
        shippingAddressDTO.setCity("Vancouver");
        shippingAddressDTO.setCountry("Canada");
        shippingAddressDTO.setPostalCode("ABC123");
        shippingAddressDTO.setStreetName("123 Street name");

        AddressDTO billingAddressDTO = new AddressDTO();
        shippingAddressDTO.setType("billing");
        shippingAddressDTO.setCity("Vancouver");
        shippingAddressDTO.setCountry("Canada");
        shippingAddressDTO.setPostalCode("ABC123");
        shippingAddressDTO.setStreetName("123 Street name");

        List<AddressDTO> addresses = new ArrayList<>();
        addresses.add(shippingAddressDTO);
        addresses.add(billingAddressDTO);

        return addresses;
    }

}
