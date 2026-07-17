-- ============================================================================
-- schema.sql
-- ----------------------------------------------------------------------------
-- Reference schema for the `users` table expected by JdbcUserDao.
--
-- This is written in ANSI-ish SQL that works with only minor tweaks across
-- MySQL, PostgreSQL, and SQL Server. Adjust types as needed for your chosen
-- database (see the notes under each column).
--
-- HOW TO USE:
--   1. Create a database/schema for the hospital app in your DB of choice
--      (e.g. `CREATE DATABASE hospital_db;`).
--   2. Run this script against it (e.g. `mysql hospital_db < schema.sql`,
--      or `psql hospital_db -f schema.sql`, or via your IDE's DB console).
--   3. Configure DatabaseConfig.java with the connection details.
-- ============================================================================

CREATE TABLE IF NOT EXISTS users (
                                     id               INTEGER      PRIMARY KEY AUTO_INCREMENT, -- MySQL: AUTO_INCREMENT
    -- PostgreSQL: use "SERIAL" or "GENERATED ALWAYS AS IDENTITY"
    -- SQL Server: use "IDENTITY(1,1)"
                                     full_name        VARCHAR(100) NOT NULL,
    username         VARCHAR(20)  NOT NULL UNIQUE,
    email            VARCHAR(100) NOT NULL UNIQUE,
    phone_number     VARCHAR(20)  NOT NULL,
    hashed_password  VARCHAR(255) NOT NULL,   -- store only hashed passwords, never plaintext
    role             VARCHAR(20)  NOT NULL,   -- 'PATIENT' | 'DOCTOR' | 'ADMINISTRATOR' (matches Role enum name())
    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
    );

-- Helpful indexes for the lookups JdbcUserDao performs most often.
CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);
CREATE INDEX IF NOT EXISTS idx_users_email    ON users (email);

-- ----------------------------------------------------------------------------
-- Optional: seed the same demo administrator account that
-- AuthService/InMemoryUserDao ships with by default, so behaviour stays
-- consistent when you switch from in-memory to a real database.
--
-- NOTE: the hashed_password value below is a SHA-256 hash of "Admin123",
-- matching AuthService.hashPassword's algorithm. Replace it if you change
-- the hashing approach (recommended: BCrypt/Argon2 for production).
-- ----------------------------------------------------------------------------
-- INSERT INTO users (full_name, username, email, phone_number, hashed_password, role)
-- VALUES (
--     'System Administrator',
--     'admin',
--     'admin@cityhospital.org',
--     '+10000000000',
--     '<sha256-hash-of-Admin123-goes-here>',
--     'ADMINISTRATOR'
-- );
