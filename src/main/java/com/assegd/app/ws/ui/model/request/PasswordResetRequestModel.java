package com.assegd.app.ws.ui.model.request;

import org.hibernate.annotations.common.reflection.XProperty;

public class PasswordResetRequestModel {

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
