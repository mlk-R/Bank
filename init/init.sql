-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- Создание таблицы счетов
CREATE TABLE IF NOT EXISTS accounts (
                                        id SERIAL PRIMARY KEY,
                                        user_id INTEGER NOT NULL,
                                        balance NUMERIC(19,4) NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    card_number VARCHAR(16) NOT NULL UNIQUE,
    expiration_date DATE NOT NULL,
    cvv VARCHAR(3) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE CASCADE
    );

-- Создание таблицы займов
CREATE TABLE IF NOT EXISTS loans (
                                     id SERIAL PRIMARY KEY,
                                     user_id INTEGER NOT NULL,
                                     amount NUMERIC(19,4) NOT NULL,
    interest_rate NUMERIC(10,4) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_loans_user FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE CASCADE
    );

-- Создание таблицы депозитов
CREATE TABLE IF NOT EXISTS deposits (
                                        id SERIAL PRIMARY KEY,
                                        user_id INTEGER NOT NULL,
                                        amount NUMERIC(19,4) NOT NULL,
    term INTEGER NOT NULL,
    interest_rate NUMERIC(10,4) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_deposits_user FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE CASCADE
    );

-- Создание таблицы транзакций
CREATE TABLE IF NOT EXISTS transactions (
                                            id SERIAL PRIMARY KEY,
                                            account_id INTEGER NOT NULL,
                                            type VARCHAR(50) NOT NULL,
    amount NUMERIC(19,4) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    external_id VARCHAR(255) UNIQUE,
    CONSTRAINT fk_transactions_account FOREIGN KEY (account_id)
    REFERENCES accounts (id) ON DELETE CASCADE
    );

-- Создание таблицы транзакции
CREATE INDEX idx_external_id ON transactions (external_id);
