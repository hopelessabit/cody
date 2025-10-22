package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.constant.Role;
import cody.ecommerce.cody_app.dto.UserDTO;
import cody.ecommerce.cody_app.exception.NotFoundException;
import org.springframework.data.domain.Page;

public interface AccountService {

    /**
     * Retrieves an account by its unique identifier.
     *
     * @param id the unique ID of the user account
     * @return the UserDTO representing the account details
     * @throws NotFoundException if the account is not found
     */
    UserDTO getById(String id) throws NotFoundException;

    /**
     * Searches for accounts based on keyword and optional role filter.
     *
     * @param keyword the search keyword to match against account names and emails
     * @param role the optional role to filter accounts by
     * @param page the page number for pagination
     * @param size the number of accounts per page
     * @param sortBy the field to sort by
     * @param sortDirection the direction of sorting ("ASC" or "DESC")
     * @return a Page of UserDTO containing search results
     */
    Page<UserDTO> searchAccounts(String keyword, Role role, int page, int size,
                                 String sortBy, String sortDirection);
}
