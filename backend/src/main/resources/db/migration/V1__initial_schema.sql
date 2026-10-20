CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  full_name VARCHAR(120) NOT NULL,
  email VARCHAR(254) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE accounts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  type VARCHAR(30) NOT NULL,
  opening_balance DECIMAL(15,2) NOT NULL DEFAULT 0,
  currency CHAR(3) NOT NULL DEFAULT 'INR',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_accounts_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT uq_account_name UNIQUE(user_id,name),
  CONSTRAINT ck_account_type CHECK(type IN ('CASH','SAVINGS','OTHER'))
);
CREATE TABLE transactions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  type VARCHAR(20) NOT NULL,
  amount DECIMAL(15,2) NOT NULL,
  category VARCHAR(60),
  source_account_id BIGINT NOT NULL,
  destination_account_id BIGINT,
  description VARCHAR(240) NOT NULL,
  payment_method VARCHAR(40) NOT NULL,
  transaction_date DATE NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_transactions_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_transactions_source FOREIGN KEY(source_account_id) REFERENCES accounts(id),
  CONSTRAINT fk_transactions_destination FOREIGN KEY(destination_account_id) REFERENCES accounts(id),
  CONSTRAINT ck_transaction_type CHECK(type IN ('INCOME','EXPENSE','TRANSFER')),
  CONSTRAINT ck_transaction_amount CHECK(amount > 0),
  CONSTRAINT ck_transfer_accounts CHECK((type='TRANSFER' AND destination_account_id IS NOT NULL AND destination_account_id <> source_account_id) OR (type<>'TRANSFER' AND destination_account_id IS NULL))
);
CREATE INDEX idx_tx_user_date ON transactions(user_id,transaction_date DESC);
CREATE INDEX idx_tx_user_type_date ON transactions(user_id,type,transaction_date);
