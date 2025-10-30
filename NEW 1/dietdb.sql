-- Run this SQL to create the database, users and sample data.
CREATE DATABASE IF NOT EXISTS dietdb;
USE dietdb;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  full_name VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS diet_plans (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  date_plan DATE NOT NULL,
  meal_time VARCHAR(50),
  items TEXT,
  calories INT,
  notes TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- sample user (username: test, password: test123)
INSERT INTO users (username, password, full_name) 
SELECT 'test','test123','Test User' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='test');
