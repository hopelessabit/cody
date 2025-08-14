package cody.ecommerce.cody_app.dto.request;

import cody.ecommerce.cody_app.dto.Error;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public Error<?> validate() {
        Map<String, String> errors = new HashMap<>();
        if (firstName == null || firstName.isEmpty()) {
            errors.put("firstName", "First name is required");
        }

        if (lastName == null || lastName.isEmpty()) {
            errors.put("lastName", "Last name is required");
        }

        if (email == null || email.isEmpty()) {
            errors.put("email", "Email is required");
        } else if (!email.matches("^[\\w-\\.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            errors.put("email", "Invalid email format");
        }

        if (password == null || password.isEmpty()) {
            errors.put("password", "Password is required");
        } else if (password.length() < 6) {
            errors.put("password", "Password must be at least 6 characters long");
        }

        if (confirmPassword == null){
            errors.put("confirmPassword", "Confirm password is required");
        } else if (!confirmPassword.equals(password)) {
            errors.put("confirmPassword", "Passwords do not match");
        }
        if (errors.isEmpty()) {
            return null;
        }
        return Error.build("Bad request", errors); // No errors found
    }
}
