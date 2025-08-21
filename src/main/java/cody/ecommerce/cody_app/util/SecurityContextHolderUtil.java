package cody.ecommerce.cody_app.util;

import cody.ecommerce.cody_app.constant.Role;
import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.entity.User;
import cody.ecommerce.cody_app.exception.InternalServerErrorException;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextHolderUtil {
    private SecurityContextHolderUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Role getRole(){
        try {
            var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof String){
                return null;
            }
            return ((User) principal).getRole();
        } catch (Exception e){
            throw new InternalServerErrorException("Cannot get role from SecurityContextHolder", Error.build(e.getMessage()));
        }
    }

    public static String getAccountId(){
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    public static User getAccount(){
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
