package com.assegd.app.ws.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UtilsTest {

    @Autowired
    Utils utils;

    @BeforeEach
    void setup() throws Exception {

    }

    @Test
    final void testGenerateUserId() {
        String userId = utils.generateUserId(30);
        String userId2 = utils.generateUserId(30);
        assertNotNull(userId);
        assertNotNull(userId2);

        assertTrue(userId.length() == 30);
        assertTrue(!userId.equalsIgnoreCase(userId2));


    }

    /* The first test for hastokenexpired
    @Test
    //@Disabled
    final void testHasTokenExpired() {
        //hardcoded
        //Make sure to update this token, this test case will fail if the token has exprired (for the hardcoded one)
        //String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJDWE9aU1BxTW5aU3F0Snl6Nk5DSlJNRWdkZEdPRW8iLCJleHAiOjE1ODg5NDE0MjR9.mA4s8u_ltj0lpft1DnNEVAFnofXIz5brY59CUxX2i_SBTWGdR4gRrz5n-I8oMFTvJ_upcRZhnjIrMO2_hgnh-Q";

       // generated token value, for instance we are using the email verification token generator to simply generate a token
        String token = utils.generateEmailVerificationToken("dfjskadhfjksdaf");
        assertNotNull(token);

        boolean hasTokenExpired = Utils.hasTokenExpired(token);
        assertFalse(hasTokenExpired);
    }*/

    @Test
    final void testHasTokenNotExpired() {
        String token = utils.generateEmailVerificationToken("dfjskadhfjksdaf");
        assertNotNull(token);

        boolean hasTokenExpired = Utils.hasTokenExpired(token);
        assertFalse(hasTokenExpired);
    }

    @Test
    final void testHasTokenExpired() {
        String expiredToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiZXhwIjoxNTg4NDk1ODAzfQ.ScCEuhiJD8fv2kc-yQl-yFzGreY5z4zIUaZs9FH9K8hmGqPdOuS529JJ67RF6VehRiiiKyfOOcbTMAJQilQqjg";
        boolean hasTokenExpired = Utils.hasTokenExpired(expiredToken);

        assertTrue(hasTokenExpired);
    }
}
