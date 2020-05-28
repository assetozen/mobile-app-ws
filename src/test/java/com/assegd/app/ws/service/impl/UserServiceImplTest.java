package com.assegd.app.ws.service.impl;


import com.assegd.app.ws.InitialUserSetup;
import com.assegd.app.ws.exceptions.UserServiceException;
import com.assegd.app.ws.io.entity.AddressEntity;
import com.assegd.app.ws.io.entity.AuthorityEntity;
import com.assegd.app.ws.io.entity.RoleEntity;
import com.assegd.app.ws.io.entity.UserEntity;
import com.assegd.app.ws.io.repositories.AuthorityRepository;
import com.assegd.app.ws.io.repositories.PasswordResetTokenRepository;
import com.assegd.app.ws.io.repositories.RoleRepository;
import com.assegd.app.ws.io.repositories.UserRepository;
import com.assegd.app.ws.shared.AmazonSES;
import com.assegd.app.ws.shared.Roles;
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
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.*;

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

    @Mock
    InitialUserSetup initialUserSetup;

    @Mock
    AuthorityRepository authorityRepository;

    @Mock
    RoleRepository roleRepository;

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
        addUserAndRolesBeforeTestStarts();
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
       // userDto.setRoles((Collection<String>) createRole(Roles.ROLE_USER.name(), Arrays.asList(createAuthority("READ_AUTHORITY"),createAuthority("WRITE_AUTHORITY"))));
        userDto.setRoles(new HashSet<>(Arrays.asList(Roles.ROLE_USER.name())));

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
        userDto.setRoles(new HashSet<>(Arrays.asList(Roles.ROLE_USER.name())));

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

    private void addUserAndRolesBeforeTestStarts(){
        System.out.println("From Application ready event....");

        AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
        AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
        AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");

        createRole(Roles.ROLE_USER.name(), Arrays.asList(readAuthority,writeAuthority));
        RoleEntity roleAdmin = createRole(Roles.ROLE_ADMIN.name(), Arrays.asList(readAuthority,writeAuthority, deleteAuthority));

        if (roleAdmin == null) return;

        UserEntity adminUser = new UserEntity();
        adminUser.setFirstName("Assegd Assefa");
        adminUser.setLastName("Asfaw");
        adminUser.setEmail("admin@user.com");
        adminUser.setEmailVerificationStatus(true);
        adminUser.setUserId(utils.generateUserId(30));
        adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode("12345678"));
        adminUser.setRoles(Arrays.asList(roleAdmin));

        userRepository.save(adminUser);
    }

    @Transactional
    private AuthorityEntity createAuthority(String name) {
        AuthorityEntity authorityEntity = authorityRepository.findByName(name);
        if (authorityEntity == null) {
            authorityEntity = new AuthorityEntity(name);
            authorityRepository.save(authorityEntity);
        }
        return authorityEntity;
    }

    @Transactional
    private RoleEntity createRole(String name, Collection<AuthorityEntity> authorities){
        RoleEntity role = roleRepository.findByName(name);
        if (role == null)
        {
            role = new RoleEntity(name);
            role.setAuthorities(authorities);
            roleRepository.save(role);
        }
        return role;
    }
}
