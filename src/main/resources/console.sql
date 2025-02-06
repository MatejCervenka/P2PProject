create database p2p_bank;

use p2p_bank;

CREATE TABLE account (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number INT NOT NULL UNIQUE,
    balance INT NOT NULL
);