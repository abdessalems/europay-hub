package com.europay.hub.features.iam.application;

import com.europay.hub.features.iam.application.dto.AuthResponse;
import com.europay.hub.features.iam.application.dto.LoginRequest;
import com.europay.hub.features.iam.application.dto.RegisterRequest;
import com.europay.hub.features.iam.application.dto.RegisterResponse;
import com.europay.hub.features.iam.domain.EmailAlreadyInUseException;
import com.europay.hub.features.iam.domain.InvalidCredentialsException;
import com.europay.hub.features.iam.domain.User;
import com.europay.hub.features.iam.domain.UserRepository;
import com.europay.hub.features.merchant.domain.Merchant;
import com.europay.hub.features.merchant.domain.MerchantRepository;
import com.europay.hub.security.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registration and login. Registration creates a merchant and its owner user atomically;
 * login verifies the password hash and issues a JWT.
 */
@Service
public class AuthService {

    private final MerchantRepository merchantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(MerchantRepository merchantRepository, UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.merchantRepository = merchantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email) || merchantRepository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException(email);
        }

        Merchant merchant = merchantRepository.save(Merchant.register(request.legalName(), email));
        User user = userRepository.save(
                User.registerMerchantUser(merchant.id(), email, passwordEncoder.encode(request.password())));

        return new RegisterResponse(
                merchant.id(), user.id(), user.email(), user.role().name(), merchant.status().name());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);
        if (!user.isActive() || !passwordEncoder.matches(request.password(), user.passwordHash())) {
            throw new InvalidCredentialsException();
        }
        return new AuthResponse(
                jwtService.generateToken(user), "Bearer", jwtService.expirationSeconds(), user.role().name());
    }
}
