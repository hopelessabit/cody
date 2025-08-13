package cody.ecommerce.cody_app.constant;

import lombok.Getter;

public enum Role {
    US ("USER","Người dùng"),
    AD ("ADMIN","Admin"),
    SL ("SALE","Quản lý");

    private final String fullName;
    private final String vietnamese;

    Role(String fullName, String vietnamese){
        this.fullName = fullName;
        this.vietnamese = vietnamese;
    }

    public String getFullName() {
        return fullName;
    }

    public String getVietnamese() {
        return vietnamese;
    }

    public boolean isUser() {
        return this == US;
    }

    public boolean isAdmin() {
        return this == AD;
    }

    public boolean isSale() {
        return this == SL;
    }
}