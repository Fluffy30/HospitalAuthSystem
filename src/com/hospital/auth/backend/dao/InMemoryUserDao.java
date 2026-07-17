package com.hospital.auth.backend.dao;

import com.hospital.auth.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InMemoryUserDao.java
 * ---------------------------------------------------------------------
 * Default {@link UserDao} implementation that stores accounts in a
 * simple in-memory map. This is what powers the application out of the
 * box, with zero database setup required - perfect for demos, UI
 * development, and testing.
 *
 * Data here is lost every time the application restarts. Once you are
 * ready to persist accounts permanently, implement the real database
 * calls in {@link JdbcUserDao} and flip
 * {@link com.hospital.auth.backend.config.DatabaseConfig#USE_DATABASE}
 * to true - no other class needs to change.
 * ---------------------------------------------------------------------
 */
public class InMemoryUserDao implements UserDao {

    /** Users keyed by lower-cased username for fast, case-insensitive lookup. */
    private final Map<String, User> usersByUsername = new ConcurrentHashMap<>();

    @Override
    public void insert(User user) throws DataAccessException {
        String key = user.getUsername().toLowerCase();
        if (usersByUsername.containsKey(key)) {
            throw new DataAccessException("Username already exists: " + user.getUsername());
        }
        usersByUsername.put(key, user);
    }

    @Override
    public Optional<User> findByUsername(String username) throws DataAccessException {
        if (username == null) return Optional.empty();
        return Optional.ofNullable(usersByUsername.get(username.trim().toLowerCase()));
    }

    @Override
    public Optional<User> findByEmail(String email) throws DataAccessException {
        if (email == null) return Optional.empty();
        return usersByUsername.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }

    @Override
    public void update(User user) throws DataAccessException {
        String key = user.getUsername().toLowerCase();
        if (!usersByUsername.containsKey(key)) {
            throw new DataAccessException("Cannot update - no such user: " + user.getUsername());
        }
        usersByUsername.put(key, user);
    }

    @Override
    public boolean existsByUsername(String username) throws DataAccessException {
        return username != null && usersByUsername.containsKey(username.trim().toLowerCase());
    }

    @Override
    public boolean existsByEmail(String email) throws DataAccessException {
        return findByEmail(email).isPresent();
    }

    @Override
    public List<User> findAll() throws DataAccessException {
        return new ArrayList<>(usersByUsername.values());
    }
}
