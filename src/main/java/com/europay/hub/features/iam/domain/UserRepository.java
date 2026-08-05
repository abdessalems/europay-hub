package com.europay.hub.features.iam.domain;

import java.util.Optional;

/**
 * Domain port for persisting and loading {@link User} aggregates.
 * Implemented by an adapter in the infrastructure layer.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
