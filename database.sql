-- USERS
CREATE TABLE users (
    id VARCHAR(50) PRIMARY KEY,
    email NVARCHAR(255),
    password NVARCHAR(255),
    name NVARCHAR(100),
    role VARCHAR(20),
    created_at DATETIME
);

-- POSTS
CREATE TABLE posts (
    id VARCHAR(50) PRIMARY KEY,
    author_id VARCHAR(50),
    title NVARCHAR(255),
	description NVARCHAR(500),
    slug VARCHAR(255),
	meta_title VARCHAR(255),
	meta_description NVARCHAR(500),
    content NVARCHAR(MAX),
    category_type NVARCHAR(100),
    published_at DATETIME,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (author_id) REFERENCES users(id)
);

-- TASKS
CREATE TABLE tasks (
    id VARCHAR(50) PRIMARY KEY,
    title NVARCHAR(255),
    description NVARCHAR(MAX),
    due_date DATETIME,
	create_by VARCHAR(50),
    status NVARCHAR(50),
    created_at DATETIME,
    FOREIGN KEY (create_by) REFERENCES users(id)
);

-- EMPLOYEE_TASKS
CREATE TABLE employee_tasks (
    task_id VARCHAR(50),
    assign_to VARCHAR(50),
    assign_by VARCHAR(50),
    score DECIMAL(5,2),
    evaluation_period NVARCHAR(50),
    PRIMARY KEY (task_id, assign_to),
    FOREIGN KEY (assign_to) REFERENCES users(id),
    FOREIGN KEY (assign_by) REFERENCES users(id),
    FOREIGN KEY (task_id) REFERENCES tasks(id)
);

-- ACHIEVEMENTS
CREATE TABLE achievements (
    id VARCHAR(50) PRIMARY KEY,
    name NVARCHAR(255),
    description NVARCHAR(MAX),
    created_at DATETIME
);

-- USER_ACHIEVEMENTS
CREATE TABLE user_achievements (
    achievement_id VARCHAR(50),
    user_id VARCHAR(50),
    awarded_at DATETIME,
    PRIMARY KEY (achievement_id, user_id),
    FOREIGN KEY (achievement_id) REFERENCES achievements(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- TRACK_BY
CREATE TABLE track_by (
    id VARCHAR(50) PRIMARY KEY,
    name NVARCHAR(100),
    map_to NVARCHAR(50)
);

-- KPIS
CREATE TABLE kpis (
    id VARCHAR(50) PRIMARY KEY,
    name NVARCHAR(255),
    description NVARCHAR(MAX),
    weight INT,
    created_at DATETIME,
	create_by VARCHAR(50),
    FOREIGN KEY (create_by) REFERENCES users(id)
);

-- KPI_TRACK_BY
CREATE TABLE kpi_track_by(
	kpi_id VARCHAR(50),
	track_by_id VARCHAR(50),
    PRIMARY KEY (kpi_id, track_by_id),
    FOREIGN KEY (kpi_id) REFERENCES kpis(id),
    FOREIGN KEY (track_by_id) REFERENCES track_by(id)
);

-- EMPLOYEE_KPIS
CREATE TABLE employee_kpis (
	id VARCHAR(50) PRIMARY KEY,
    kpi_id VARCHAR(50),
    assign_to VARCHAR(50),
    assign_by VARCHAR(50),
    score DECIMAL(5,2),
    evaluation_period NVARCHAR(50),
    FOREIGN KEY (assign_to) REFERENCES users(id),
    FOREIGN KEY (assign_by) REFERENCES users(id)
);

-- KPIS_PROGRESS
CREATE TABLE kpi_progress (
	id VARCHAR(50) PRIMARY KEY,
    kpi_id VARCHAR(50),
    employee_kpi_id VARCHAR(50),
    track_by_id VARCHAR(50),
    result NVARCHAR(50),
    create_time DATETIME,
    FOREIGN KEY (employee_kpi_id) REFERENCES employee_kpis(id),
    FOREIGN KEY (track_by_id) REFERENCES track_by(id)
);

-- EVENTS
CREATE TABLE events (
    id VARCHAR(50) PRIMARY KEY,
    create_by VARCHAR(50),
    title NVARCHAR(255),
    description NVARCHAR(MAX),
    slug VARCHAR(255),
	meta_title VARCHAR(500),
	meta_description NVARCHAR(500),
    location NVARCHAR(500),
    event_date DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (create_by) REFERENCES users(id)
);

-- PRODUCTS
CREATE TABLE products (
    id VARCHAR(50) PRIMARY KEY,
    name NVARCHAR(255),
    description NVARCHAR(MAX),
    slug VARCHAR(255),
	meta_description NVARCHAR(500),
    price DECIMAL(18,2),
    original_price DECIMAL(18,2),
    stock_quantity INT,
    created_at DATETIME,
    updated_at DATETIME
);

-- PRODUCT_IMAGES
CREATE TABLE product_images (
    id VARCHAR(50) PRIMARY KEY,
    product_id VARCHAR(50),
    image_url NVARCHAR(700),
    is_main BIT,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- PRODUCT_INCLUDED
CREATE TABLE product_included (
    product_id VARCHAR(50),
    included_product_id VARCHAR(50),
    PRIMARY KEY (product_id, included_product_id),
	FOREIGN KEY (product_id) REFERENCES products(id),
	FOREIGN KEY (included_product_id) REFERENCES products(id)
);

-- CART
CREATE TABLE carts (
    user_id VARCHAR(50),
    product_id VARCHAR(50),
    quantity INT,
	create_at DATETIME,
	updated_at DATETIME,
	PRIMARY KEY (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- CATEGORIES
CREATE TABLE categories (
    id VARCHAR(50) PRIMARY KEY,
    name NVARCHAR(100),
    description NVARCHAR(MAX),
    created_at DATETIME,
    updated_at DATETIME
);

-- PRODUCT_CATEGORIES
CREATE TABLE product_categories (
    product_id VARCHAR(50),
    category_id VARCHAR(50),
    PRIMARY KEY (product_id, category_id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- INGREDIENTS
CREATE TABLE ingredients (
    id VARCHAR(50) PRIMARY KEY,
    name NVARCHAR(255)
);

-- PRODUCT_INGREDIENTS
CREATE TABLE product_ingredients (
    product_id VARCHAR(50),
    ingredient_id VARCHAR(50),
    PRIMARY KEY (product_id, ingredient_id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);

-- ORDERS
CREATE TABLE orders (
    id VARCHAR(50) PRIMARY KEY,
    seller_id VARCHAR(50),
    buyer_id VARCHAR(50),
    total_price DECIMAL(18,2),
    current_status NVARCHAR(50),
    buyer_rating INT,
    created_at DATETIME,
    FOREIGN KEY (buyer_id) REFERENCES users(id),
    FOREIGN KEY (seller_id) REFERENCES users(id)
);

-- ORDER_ITEMS
CREATE TABLE order_items (
    order_id VARCHAR(50),
    product_id VARCHAR(50),
    quantity INT,
    price DECIMAL(18,2),
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ORDER_STATUS
CREATE TABLE order_status (
    order_id VARCHAR(50),
    status NVARCHAR(50),
	modifier_id VARCHAR(50),
    modified_at DATETIME,
    PRIMARY KEY (order_id, status),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (modifier_id) REFERENCES users(id)
);

-- APPLICATION_INFORMATION
CREATE TABLE application_information (
    code VARCHAR(50) PRIMARY KEY,
    content NVARCHAR(2000),
    created_at DATETIME,
    is_disable BIT
);

-- APPLICATION_IMAGES
CREATE TABLE application_images (
    code VARCHAR(50) PRIMARY KEY,
    content NVARCHAR(MAX),
    created_at DATETIME,
    is_disable BIT
);
