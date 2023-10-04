START TRANSACTION;

CREATE TABLE IF NOT EXISTS clients (
    id INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS orders(
    id INT AUTO_INCREMENT NOT NULL,
    date DATE NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    client_id INT NOT NULL,
    
    PRIMARY KEY (id),
    FOREIGN KEY (client_id) REFERENCES clients(id)
);

INSERT INTO clients VALUES (1, 'Juan', 'juan@email.com');
INSERT INTO clients VALUES (2, 'Ana', 'ana@email.com');
INSERT INTO clients VALUES (3, 'Pedro', 'pedro@email.com');

INSERT INTO orders VALUES (101, '2023-01-15', 50, 1);
INSERT INTO orders VALUES (102, '2023-02-10', 30, 1);
INSERT INTO orders VALUES (103, '2023-03-05', 25, 2);
INSERT INTO orders VALUES (104, '2023-04-20', 40, 3);

COMMIT;