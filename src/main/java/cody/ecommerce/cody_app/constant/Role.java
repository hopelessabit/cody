package cody.ecommerce.cody_app.constant;

import lombok.Getter;

@Getter
public enum Role implements IEnumerate{
    US ("USER","Người dùng"),
    AD ("ADMIN","Admin"),
    EP ("EMPLOYEE","Quản lý"),
    MN ("MANAGER","Quản lý");

    final String fullName;
    final String vietnamese;

    Role(String fullName, String vietnamese){
        this.fullName = fullName;
        this.vietnamese = vietnamese;
    }

    public boolean isUser() {
        return this == US;
    }

    public boolean isAdmin() {
        return this == AD;
    }

    public boolean isEmployee() {
        return this == EP;
    }

    public boolean isManager() {
        return this == MN;
    }
}