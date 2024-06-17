package com.main.library.session;

import com.main.library.dto.admin.AdminDTO;

public class AdminSession {
    private static AdminSession instance;
    private AdminDTO user;

    private AdminSession() {
        // private constructor to prevent instantiation
    }

    public static AdminSession getInstance() {
        if (instance == null) {
            instance = new AdminSession();
        }
        return instance;
    }

    public void setAdmin(AdminDTO user) {
        this.user = user;
    }

    public AdminDTO getUser() {
        return user;
    }

    public void purgeSession() {
        user = null;
    }
}
