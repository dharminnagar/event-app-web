-- Database schema for Event Aggregator
-- This script creates the necessary tables and inserts sample data

-- Create database if it doesn't exist 
CREATE DATABASE IF NOT EXISTS eventWebApp;

-- Use the database
USE eventWebApp;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    password VARCHAR(100) NOT NULL,
    user_type ENUM('CUSTOMER', 'VENDOR', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Vendors table
CREATE TABLE IF NOT EXISTS vendors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    business_name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    service_type VARCHAR(50) NOT NULL,
    base_cost DECIMAL(10, 2) NOT NULL,
    location VARCHAR(100) NOT NULL,
    contact_email VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    rating DECIMAL(3, 2) DEFAULT 0.0,
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Cart items table
CREATE TABLE IF NOT EXISTS cart_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    vendor_id INT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE,
    UNIQUE (user_id, vendor_id)
);

-- Bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    event_date DATE NOT NULL,
    event_location VARCHAR(100) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    total_cost DECIMAL(10, 2) NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Booking vendors table (many-to-many relationship between bookings and vendors)
CREATE TABLE IF NOT EXISTS booking_vendors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    vendor_id INT NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE,
    UNIQUE (booking_id, vendor_id)
);

-- Reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    vendor_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE
);

-- Insert sample users if they don't already exist
INSERT IGNORE INTO users (name, email, phone, password, user_type)
VALUES 
    ('Admin User', 'admin@eventapp.com', '1234567890', 'admin123', 'ADMIN'),
    ('Sample Vendor', 'vendor@eventapp.com', '9876543210', 'vendor123', 'VENDOR'),
    ('Sample Customer', 'customer@eventapp.com', '5555555555', 'customer123', 'CUSTOMER'),
    ('John Doe', 'john.doe@example.com', '1112223333', 'password123', 'CUSTOMER'),
    ('Jane Smith', 'jane.smith@example.com', '4445556666', 'password123', 'CUSTOMER'),
    ('Catering Services Inc', 'catering@example.com', '7778889999', 'vendor123', 'VENDOR'),
    ('Party Planners Ltd', 'planner@example.com', '3334445555', 'vendor123', 'VENDOR');

-- Insert sample vendors linked to vendor users
INSERT IGNORE INTO vendors (user_id, business_name, description, service_type, base_cost, location, contact_email, contact_phone, rating, image_url) 
VALUES 
    (2, 'Elite Events', 'Premium event planning services for all occasions.', 'Event Planning', 1500.00, 'New York, NY', 'vendor@eventapp.com', '9876543210', 4.8, '/resources/images/vendors/elite-events.jpg'),
    (6, 'Gourmet Delights Catering', 'Exquisite catering for all types of events.', 'Catering', 1200.00, 'Chicago, IL', 'catering@example.com', '7778889999', 4.5, '/resources/images/vendors/catering.jpg'),
    (7, 'Party Planning Pros', 'Full service event planning and coordination.', 'Event Planning', 2500.00, 'Los Angeles, CA', 'planner@example.com', '3334445555', 4.9, '/resources/images/vendors/party-planners.jpg');

-- Insert sample reviews
INSERT IGNORE INTO reviews (user_id, vendor_id, rating, comment)
VALUES
    (3, 1, 5, 'Amazing service! They made our wedding day perfect.'),
    (4, 2, 4, 'Food was delicious, but they arrived a bit late.'),
    (5, 3, 5, 'Absolutely incredible. They handled everything seamlessly.');

-- Insert sample bookings
INSERT IGNORE INTO bookings (user_id, event_date, event_location, status, total_cost)
VALUES
    (3, '2025-06-15', 'Grand Hyatt New York', 'CONFIRMED', 3500.00),
    (4, '2025-07-22', 'Chicago Convention Center', 'PENDING', 2200.00),
    (5, '2025-08-10', 'Sunset Plaza, Los Angeles', 'CONFIRMED', 4800.00);

-- Insert sample booking-vendor relationships
INSERT IGNORE INTO booking_vendors (booking_id, vendor_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 1),
    (3, 3);