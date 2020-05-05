package com.assegd.app.ws.service.impl;


import com.assegd.app.ws.exceptions.UserServiceException;
import com.assegd.app.ws.io.entity.AddressEntity;
import com.assegd.app.ws.io.entity.UserEntity;
import com.assegd.app.ws.io.repositories.PasswordResetTokenRepository;
import com.assegd.app.ws.io.repositories.UserRepository;
import com.assegd.app.ws.shared.AmazonSES;
import com.assegd.app.ws.shared.Utils;
import com.assegd.app.ws.shared.dto.AddressDTO;
import com.assegd.app.ws.shared.dto.UserDto;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    Utils utils;

    @Mock
    AmazonSES amazonSES;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    String userId = "sdfklsdklfjsksd";
    String encryptedPassword = "klsdjfeworiiofsdklfj23232";

    UserEntity userEntity;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("Assegd Assefa");
        userEntity.setUserId(userId);
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setEmail("assetozen@gmail.com");
        userEntity.setEmailVerificationToken("dsklfjsdklfjskajklsfjskljfs");
        userEntity.setAddresses(getAddressesEntity());
    }

    @Test
    final void testGetUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = userService.getUser("assetozen@gmail.com");


        assertNotNull(userDto);

        assertEquals("Assegd Assefa", userDto.getFirstName());
    }

    @Test
    final void testGetUser_UsernameNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> {
                    userService.getUser("assetozen@gmail.com");
                });
    }

    @Test
    final void testCreateUser_CreateUserServiceException(){
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName("Assegd Assefa");
        userDto.setLastName("Asfaw");
        userDto.setPassword("12345678");
        userDto.setEmail("assetozen@gmail.com");

        assertThrows(UserServiceException.class, () ->{
            userService.createUser(userDto);
        });
    }


    @Test()
    final void testCreateUser() {

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn("dlfkjdklfjsdlfs");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        //when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity); // any() doesnt accept null class instance so i used mockito.any()...
        when(userRepository.save(Mockito.any(UserEntity.class))).thenReturn(userEntity);
        //Mockito.doNothing().when(amazonSES).verifyEmail(any(UserDto.class)); // any() doesnt accept null class instance so i used mockito.any()...
        Mockito.doNothing().when(amazonSES).verifyEmail(Mockito.any(UserDto.class));

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName("Assegd Assefa");
        userDto.setLastName("Asfaw");
        userDto.setPassword("12345678");
        userDto.setEmail("assetozen@gmail.com");

        UserDto storedUserDetails = userService.createUser(userDto);

        assertNotNull(storedUserDetails);
        assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
        assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
        assertNotNull(storedUserDetails.getUserId());
        assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
        verify(utils, times(storedUserDetails.getAddresses().size())).generateAddressId(30);
//        verify(utils, times(2)).generateAddressId(30);
        verify(bCryptPasswordEncoder, times(1)).encode("12345678");
        verify(userRepository, times(1)).save(Mockito.any(UserEntity.class));
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

    private List<AddressEntity> getAddressesEntity() {
        List<AddressDTO> addresses = getAddressesDto();

        Type listType = new TypeToken<List<AddressEntity>>() {
        }.getType();

        return new ModelMapper().map(addresses, listType);
    }
}
