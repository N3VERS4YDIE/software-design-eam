CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    category_id INT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

INSERT INTO categories (name) VALUES ('Board');
INSERT INTO categories (name) VALUES ('GPU');
INSERT INTO categories (name) VALUES ('CPU');
INSERT INTO categories (name) VALUES ('RAM');
INSERT INTO categories (name) VALUES ('Monitor');

INSERT INTO products (name, price, category_id) VALUES ('ASUS', 150, 1);
INSERT INTO products (name, price, category_id) VALUES ('MSI', 120, 1);
INSERT INTO products (name, price, category_id) VALUES ('Gigabyte', 100, 1);
INSERT INTO products (name, price, category_id) VALUES ('ASRock', 80, 1);
INSERT INTO products (name, price, category_id) VALUES ('Biostar', 70, 1);

INSERT INTO products (name, price, category_id) VALUES ('RTX 2080', 800, 2);
INSERT INTO products (name, price, category_id) VALUES ('RTX 2070', 600, 2);
INSERT INTO products (name, price, category_id) VALUES ('RTX 2060', 400, 2);
INSERT INTO products (name, price, category_id) VALUES ('GTX 1080', 500, 2);
INSERT INTO products (name, price, category_id) VALUES ('GTX 1070', 400, 2);

INSERT INTO products (name, price, category_id) VALUES ('i9', 600, 3);
INSERT INTO products (name, price, category_id) VALUES ('i7', 400, 3);
INSERT INTO products (name, price, category_id) VALUES ('R9', 650, 3);
INSERT INTO products (name, price, category_id) VALUES ('R7', 450, 3);

INSERT INTO products (name, price, category_id) VALUES ('16GB', 100, 4);
INSERT INTO products (name, price, category_id) VALUES ('8GB', 50, 4);
INSERT INTO products (name, price, category_id) VALUES ('4GB', 25, 4);
INSERT INTO products (name, price, category_id) VALUES ('2GB', 15, 4);

INSERT INTO products (name, price, category_id) VALUES ('4K', 300, 5);
INSERT INTO products (name, price, category_id) VALUES ('2K', 200, 5);
INSERT INTO products (name, price, category_id) VALUES ('1080p', 100, 5);


-- SELECT P.*, C.* FROM products P
-- INNER JOIN categories C
-- ON P.category_id = C.id
-- WHERE C.id = 1;