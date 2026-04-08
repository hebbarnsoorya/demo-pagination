CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       role VARCHAR(50),
                       status VARCHAR(20) DEFAULT 'Active',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed data for testing
INSERT INTO users (name, email, role, status) VALUES
                                                  ('Admin User', 'admin@enterprise.com', 'Administrator', 'Active'),
                                                  ('John Doe', 'john@example.com', 'Editor', 'Inactive');