create database p2p_bank;

use p2p_bank;

CREATE TABLE bank (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ip_address VARCHAR(255) NOT NULL
);

CREATE TABLE account (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number INT NOT NULL UNIQUE,
    balance DOUBLE NOT NULL,
    bank_id INT NOT NULL,
    FOREIGN KEY (bank_id) REFERENCES bank(id)
);
