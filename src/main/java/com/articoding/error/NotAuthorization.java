package com.articoding.error;

import com.articoding.model.Role;

public class NotAuthorization extends RestError {

    public NotAuthorization(Role role, String action) {
        super(String.format("A %s user cannot %s", role.getName(), action));
    }

    public NotAuthorization(String action) {
        super(String.format("Logged user cannot %s", action));
    }

}
