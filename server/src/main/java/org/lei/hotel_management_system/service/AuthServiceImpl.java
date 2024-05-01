package org.lei.hotel_management_system.service;

import org.lei.hotel_management_system.DTO.TokenDTO;
import org.lei.hotel_management_system.DTO.UserLoginDTO;
import org.lei.hotel_management_system.DTO.UserRegisterDTO;
import org.lei.hotel_management_system.entity.User;
import org.lei.hotel_management_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Provides authentication services for handling user logins and registrations.
 * This service manages user authentication with the security framework and token management using JWTs.
 * It supports operations for user login and registration, each returning a tokenized response upon success.
 */
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Authenticates a user based on username and password, and generates a JWT token upon successful authentication.
     *
     * @param loginUser A {@link UserLoginDTO} object containing the user's login credentials.
     * @return A {@link TokenDTO} containing the JWT token.
     * @throws RuntimeException If the username or password is incorrect.
     */
    @Override
    public TokenDTO login(UserLoginDTO loginUser) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
            return jwtUtil.createTokenJson(loginUser.getUsername());
        } catch (Exception e) {
            throw new RuntimeException("Username or password is incorrect!");
        }
    }

    /**
     * Registers a new user with provided user details and returns a JWT token.
     *
     * @param registerUser A {@link UserRegisterDTO} object containing the user's registration details.
     * @return A {@link TokenDTO} containing the JWT token for the newly registered user.
     */
    @Override
    public TokenDTO register(UserRegisterDTO registerUser) {
        User user = userService.addUser(new User(registerUser.getUsername(), registerUser.getPassword(), registerUser.getRealName(), registerUser.getEmail(), registerUser.getPhoneNumber()));
        return jwtUtil.createTokenJson(user.getUsername());
    }
}
