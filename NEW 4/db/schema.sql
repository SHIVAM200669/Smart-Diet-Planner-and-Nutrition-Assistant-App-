CREATE DATABASE IF NOT EXISTS food_diet_planner;
USE food_diet_planner;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(100) NOT NULL,
  age INT DEFAULT NULL,
  weight FLOAT DEFAULT NULL,
  height FLOAT DEFAULT NULL,
  preferences TEXT DEFAULT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS gps_tracking (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  activity_type VARCHAR(50) NOT NULL,
  latitude DOUBLE NOT NULL,
  longitude DOUBLE NOT NULL,
  distance_km DOUBLE DEFAULT 0,
  calories_burned DOUBLE DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS goals (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  goal_title VARCHAR(255) NOT NULL,
  target VARCHAR(255) DEFAULT NULL,
  deadline DATE DEFAULT NULL,
  status VARCHAR(50) DEFAULT 'pending',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT IGNORE INTO users (username, password, age, weight, height, preferences)
VALUES ('admin', 'admin', 25, 70, 170, 'Demo user');
