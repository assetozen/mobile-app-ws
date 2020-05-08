package com.assegd.app.ws.io.repositories;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.assegd.app.ws.io.entity.AddressEntity;
import com.assegd.app.ws.io.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    boolean recordsCreated = false;

    String userId = "1a2b3c";

    @BeforeEach
    void setup() throws Exception {
        if (!recordsCreated)
            createRecords();
    }

    @Test
    final void testGetVerifiedUsers() {
        Pageable pageableRequest = PageRequest.of(1, 1);
        Page<UserEntity> page1 = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);

        assertNotNull(page1);

        List<UserEntity> userEntities = page1.getContent();
        assertNotNull(userEntities);
        assertTrue(userEntities.size() == 1);
    }

    @Test
    final void testFindUserByFirstName() {
        String firstName = "Assegd Assefa";
        List<UserEntity> users = userRepository.findUserByFirstName(firstName);

        assertNotNull(users);
        assertTrue(users.size() == 2);

        UserEntity user = users.get(0);
        assertTrue(user.getFirstName().equals(firstName));
    }

    @Test
    final void testFindUserBylastName() {
        String lastName = "Asfaw";
        List<UserEntity> users = userRepository.findUserByLastName(lastName);

        assertNotNull(users);
        assertTrue(users.size() == 2);

        UserEntity user = users.get(0);
        assertTrue(user.getLastName().equals(lastName));
    }

    @Test
    final void testFindUserByKeyword() {
        String keyword = "Asfaw";
        List<UserEntity> users = userRepository.findUserByKeyword(keyword);

        assertNotNull(users);
        assertTrue(users.size() == 2);

        UserEntity user = users.get(0);
        assertTrue(
                user.getLastName().contains(keyword) ||
                        user.getFirstName().contains(keyword)
        );
    }

    @Test
    final void testFindUserFirstNameAndLastNameByKeyword() {
        String keyword = "Asfaw";
        List<Object[]> users = userRepository.findUserFirstNameAndLastNameByKeyword(keyword);

        assertNotNull(users);
        assertTrue(users.size() == 2);

        Object[] user = users.get(0);

        assertTrue(user.length == 2);

        String userFirstName = String.valueOf(user[0]);
        String userLastName = String.valueOf(user[1]);

        assertNotNull(userFirstName);
        assertNotNull(userLastName);

        System.out.println("First name = " + userFirstName);
        System.out.println("Last name = " + userLastName);
    }

    @Test
    final void testUpdateUserEmailVerificationStatus() {
        boolean newEmailVerificationStatus = true;
        userRepository.updateUserEmailVerificationStatus(newEmailVerificationStatus, userId);
        UserEntity storedUserDetails = userRepository.findByUserId(userId);
        boolean storedEmailVerificationStatus = storedUserDetails.getEmailVerificationStatus();

        assertTrue(storedEmailVerificationStatus == newEmailVerificationStatus);
    }

    @Test
    final void testFindUserEntityByUserId(){
        UserEntity userEntity = userRepository.findUserEntityByUserId(userId);
        assertNotNull(userEntity);
        assertTrue(userEntity.getUserId().equals(userId));
    }

    @Test
    final void testGetUserEntityFullNameById(){
        List<Object[]> users = userRepository.getUserEntityFullNameById(userId);

        assertNotNull(users);
        assertTrue(users.size() == 1);

        Object[] user = users.get(0);

        String userFirstName = String.valueOf(user[0]);
        String userLastName = String.valueOf(user[1]);

        assertNotNull(userFirstName);
        assertNotNull(userLastName);
    }

    @Test
    final void testUpdateUserEntityEmailVerificationStatus() {
        boolean newEmailVerificationStatus = false;
        userRepository.updateUserEntityEmailVerificationStatus(newEmailVerificationStatus, userId);
        UserEntity storedUserDetails = userRepository.findByUserId(userId);
        boolean storedEmailVerificationStatus = storedUserDetails.getEmailVerificationStatus();

        assertTrue(storedEmailVerificationStatus == newEmailVerificationStatus);
    }

    private void createRecords() {
        // Prepare User Entity
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName("Assegd Assefa");
        userEntity.setLastName("Asfaw");
        userEntity.setUserId("1a2b3c");
        userEntity.setEncryptedPassword("xxx");
        userEntity.setEmail("assetozen@gmail.com");
        userEntity.setEmailVerificationStatus(true);

        //Prepare User Address
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setType("shipping");
        addressEntity.setAddressId("alkdjfkldf");
        addressEntity.setCity("Vancouver");
        addressEntity.setCountry("Canada");
        addressEntity.setPostalCode("ABCCDA");
        addressEntity.setStreetName("123 Street Address");

        List<AddressEntity> addresses = new ArrayList<>();
        addresses.add(addressEntity);

        userEntity.setAddresses(addresses);

        userRepository.save(userEntity);

        // Prepare User Entity 2
        UserEntity userEntity2 = new UserEntity();
        userEntity2.setFirstName("Assegd Assefa");
        userEntity2.setLastName("Asfaw");
        userEntity2.setUserId("1a2b3cjhgjhgjhgjh");
        userEntity2.setEncryptedPassword("xxx");
        userEntity2.setEmail("assetozen@gmail.com");
        userEntity2.setEmailVerificationStatus(true);

        //Prepare User Address
        AddressEntity addressEntity2 = new AddressEntity();
        addressEntity2.setType("shipping");
        addressEntity2.setAddressId("alkdjfkldfjhgjhghjgjh");
        addressEntity2.setCity("Vancouver");
        addressEntity2.setCountry("Canada");
        addressEntity2.setPostalCode("ABCCDA");
        addressEntity2.setStreetName("123 Street Address");

        List<AddressEntity> addresses2 = new ArrayList<>();
        addresses.add(addressEntity2);

        userEntity.setAddresses(addresses2);

        userRepository.save(userEntity2);

        recordsCreated = true;
    }
}
