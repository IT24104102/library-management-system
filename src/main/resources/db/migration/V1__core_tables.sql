-- V1: Core tables for LMS (MySQL 8, utf8mb4)
-- Notes:
-- - Reservation position is first-come-first-served (enforced in application logic).
-- - Loan renewals up to 2 if no reservation exists (enforced in application logic).
-- - Quantity must be >= 0; loans cannot proceed if quantity=0 (application logic).

SET NAMES utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- Roles table
CREATE TABLE roles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(128) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Users table
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(150) NOT NULL,
  email VARCHAR(190) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  status VARCHAR(32) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- User roles junction table
CREATE TABLE user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Books table
CREATE TABLE books (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  isbn VARCHAR(32) NOT NULL UNIQUE,
  title VARCHAR(255) NOT NULL,
  author VARCHAR(255) NOT NULL,
  genre VARCHAR(100) NULL,
  quantity INT NOT NULL,
  status VARCHAR(32) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CHECK (quantity >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Loans table
CREATE TABLE loans (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  book_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  checkout_at DATETIME NOT NULL,
  due_at DATETIME NOT NULL,
  returned_at DATETIME NULL,
  status VARCHAR(32) NOT NULL,
  CONSTRAINT fk_loans_book FOREIGN KEY (book_id) REFERENCES books(id),
  CONSTRAINT fk_loans_user FOREIGN KEY (user_id) REFERENCES users(id)
  -- NOTE: Renewals allowed up to 2 if no reservation exists (business rule, enforce in service layer)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Reservations table
CREATE TABLE reservations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  book_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL,
  status VARCHAR(32) NOT NULL,
  position INT NOT NULL,
  CONSTRAINT fk_reservations_book FOREIGN KEY (book_id) REFERENCES books(id),
  CONSTRAINT fk_reservations_user FOREIGN KEY (user_id) REFERENCES users(id)
  -- NOTE: position is assigned first-come-first-served (enforce in application logic)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Fines table
CREATE TABLE fines (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  loan_id BIGINT NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  status VARCHAR(32) NOT NULL,
  created_at DATETIME NOT NULL,
  paid_at DATETIME NULL,
  CONSTRAINT fk_fines_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_fines_loan FOREIGN KEY (loan_id) REFERENCES loans(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Audit log table
CREATE TABLE audit_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  actor_user_id BIGINT NOT NULL,
  action VARCHAR(150) NOT NULL,
  target_type VARCHAR(100) NOT NULL,
  target_id BIGINT NOT NULL,
  ts TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  metadata JSON NULL,
  CONSTRAINT fk_audit_actor FOREIGN KEY (actor_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
