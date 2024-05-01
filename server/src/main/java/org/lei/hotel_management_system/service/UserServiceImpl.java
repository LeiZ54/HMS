package org.lei.hotel_management_system.service;

import jakarta.servlet.http.HttpServletRequest;
import org.lei.hotel_management_system.DTO.PasswordUpdateDTO;
import org.lei.hotel_management_system.DTO.UserDetailsDTO;
import org.lei.hotel_management_system.DTO.UserRoleUpdateDTO;
import org.lei.hotel_management_system.DTO.UserUpdateDTO;
import org.lei.hotel_management_system.entity.User;
import org.lei.hotel_management_system.enums.Role;
import org.lei.hotel_management_system.repository.UserRepository;
import org.lei.hotel_management_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * Service class for managing users in the application.
 * This class provides methods to add, delete, update, and retrieve user information.
 * It handles user authentication, registration, and authorization based on roles.
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Adds a new user to the system with unique username and email.
     * Encodes the user's password before saving to the database.
     *
     * @param user the user entity to add
     * @return the saved user entity
     * @throws RuntimeException if the username or email already exists
     */
    @Override
    public User addUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null)
            throw new RuntimeException("Username already exists!");
        if (getByEmail(user.getEmail()) != null) throw new RuntimeException("Email already exists!");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Deletes a user from the system based on the provided username.
     *
     * @param username the username of the user to delete
     * @throws RuntimeException if the user does not exist, or if the user is trying to delete themselves or lacks permission
     */
    @Override
    public void deleteUser(String username) {
        if (!getCurrentUser().getRole().equals(Role.ADMIN))
            throw new RuntimeException("You do not have permission to delete this user!");
        User user = userRepository.findByUsername(username);
        if (user == null) throw new RuntimeException("User not found!");
        if (username.equals(getCurrentUser().getUsername()))
            throw new RuntimeException("You cannot delete yourself!");
        userRepository.delete(user);
    }

    /**
     * Updates the details of the current user based on provided data.
     *
     * @param updateUser data transfer object containing updated user information
     * @throws RuntimeException if the updated email already exists for another user
     */
    @Override
    public void updateUser(UserUpdateDTO updateUser) {
        User currentUser = getCurrentUser();
        if (getByEmail(updateUser.getEmail()) != null && !updateUser.getEmail().equals(currentUser.getEmail()))
            throw new RuntimeException("Email already exists!");
        if (updateUser.getEmail() != null) currentUser.setEmail(updateUser.getEmail());
        if (updateUser.getPhoneNumber() != null) currentUser.setPhoneNumber(updateUser.getPhoneNumber());
        if (updateUser.getRealName() != null) currentUser.setRealName(updateUser.getRealName());
        userRepository.save(currentUser);
    }

    /**
     * Updates the role of a specified user.
     *
     * @param updateRoleUser data transfer object containing the username and new role
     * @throws RuntimeException if the current user does not have admin permissions or the user does not exist
     */
    @Override
    public void updateUserRole(UserRoleUpdateDTO updateRoleUser) {
        if (!getCurrentUser().getRole().equals(Role.ADMIN))
            throw new RuntimeException("You do not have permission to update this user's role!");
        User user = userRepository.findByUsername(updateRoleUser.getUsername());
        user.setRole(updateRoleUser.getRole());
        userRepository.save(user);
    }

    /**
     * Updates the password of the current user.
     *
     * @param updatePassword data transfer object containing the old and new passwords
     * @throws RuntimeException if the old password does not match the current password
     */
    @Override
    public void updatePassword(PasswordUpdateDTO updatePassword) {
        User currentUser = getCurrentUser();
        if (!passwordEncoder.matches(updatePassword.getOldPassword(), currentUser.getPassword()))
            throw new RuntimeException("Password does not match!");
        currentUser.setPassword(passwordEncoder.encode(updatePassword.getNewPassword()));
        userRepository.save(currentUser);
    }

    /**
     * Retrieves the detailed information of the current user.
     *
     * @return UserDetailsDTO containing the current user's details
     */
    @Override
    public UserDetailsDTO getCurrentUserDetails() {
        return convertUserToUserDetailsDTO(getCurrentUser());
    }

    /**
     * Retrieves the detailed information of a specific user by username.
     *
     * @param username the username of the user
     * @return UserDetailsDTO containing the specified user's details
     * @throws RuntimeException if the user does not exist or the current user does not have permission to view the details
     */
    @Override
    public UserDetailsDTO getUserDetailsByUsername(String username) {
        if (getCurrentUser().getRole().equals(Role.CUSTOMER))
            throw new RuntimeException("You do not have permission to load this user!");
        return convertUserToUserDetailsDTO(userRepository.findByUsername(username));
    }

    /**
     * Retrieves the currently authenticated user based on the JWT token from the request.
     *
     * @return the currently authenticated user
     * @throws RuntimeException if the token is invalid or the user does not exist
     */
    @Override
    public User getCurrentUser() {
        // Extract the current HTTP request from the Spring context
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        // Get the Authorization header, typically containing the JWT token
        String token = request.getHeader("Authorization");
        try {
            // Decode the token to extract the username and fetch the user
            return userRepository.findByUsername(jwtUtil.getUsernameFromToken(token));
        } catch (Exception e) {
            // Handle case where token is invalid or does not decode properly
            throw new RuntimeException("Invalid token!");
        }
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address to search for
     * @return the user with the specified email
     */
    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Implements UserDetails load by username for Spring Security.
     *
     * @param username the username of the user to load
     * @return the UserDetails of the requested user
     * @throws UsernameNotFoundException if the user is not found in the database
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found!");
        }
        return user;
    }

    /**
     * Converts a User entity to a UserDetailsDTO object.
     *
     * @param user the user entity to convert
     * @return a UserDetailsDTO containing the user's information
     */
    private UserDetailsDTO convertUserToUserDetailsDTO(User user) {
        UserDetailsDTO userListDTO = new UserDetailsDTO();
        userListDTO.setUsername(user.getUsername());
        userListDTO.setEmail(user.getEmail());
        userListDTO.setPhoneNumber(user.getPhoneNumber());
        userListDTO.setRealName(user.getRealName());
        userListDTO.setRole(user.getRole());
        return userListDTO;
    }
}
