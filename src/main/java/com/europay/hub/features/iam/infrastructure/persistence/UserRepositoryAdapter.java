package com.europay.hub.features.iam.infrastructure.persistence;

import com.europay.hub.features.iam.domain.User;
import com.europay.hub.features.iam.domain.UserRepository;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Infrastructure adapter mapping the {@link UserRepository} port to JPA. */
@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpa;

    public UserRepositoryAdapter(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public User save(User user) {
        return toDomain(jpa.save(toEntity(user)));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email.trim().toLowerCase()).map(UserRepositoryAdapter::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email.trim().toLowerCase());
    }

    private static UserEntity toEntity(User user) {
        UserEntity e = new UserEntity();
        e.setId(user.id());
        e.setMerchantId(user.merchantId());
        e.setEmail(user.email());
        e.setPasswordHash(user.passwordHash());
        e.setRole(user.role());
        e.setStatus(user.status());
        e.setCreatedAt(user.createdAt());
        e.setUpdatedAt(Instant.now());
        return e;
    }

    private static User toDomain(UserEntity e) {
        return new User(e.getId(), e.getMerchantId(), e.getEmail(), e.getPasswordHash(),
                e.getRole(), e.getStatus(), e.getCreatedAt());
    }
}
