CREATE TABLE binary_contents
(
    id           BIGSERIAL PRIMARY KEY,
    file_name    VARCHAR(255),
    content_type VARCHAR(100),
    size         BIGINT
);

CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    established_date DATE NOT NULL
);

CREATE TABLE employees (
   id BIGSERIAL PRIMARY KEY,
   name VARCHAR(255) NOT NULL,
   email VARCHAR(255) UNIQUE NOT NULL,
   employee_number VARCHAR(255) UNIQUE NOT NULL,
   department_id BIGINT,
   position VARCHAR(255) NOT NULL,
   hire_date DATE NOT NULL,
   status VARCHAR NOT NULL,
   profile_image_id BIGINT,
   FOREIGN KEY (department_id) REFERENCES departments(id),
   FOREIGN KEY (profile_image_id) REFERENCES binary_contents(id)
);

CREATE TABLE change_logs (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR NOT NULL,
    employee_number VARCHAR NOT NULL,
    memo TEXT,
    ip_address VARCHAR NOT NULL,
    at TIMESTAMP NOT NULL
);

CREATE TABLE change_log_diffs (
    id BIGSERIAL PRIMARY KEY,
    change_log_id BIGINT REFERENCES change_logs(id),
    property_name VARCHAR NOT NULL,
    before TEXT,
    after TEXT
);

CREATE Table backups (
    id BIGSERIAL PRIMARY KEY,
    worker VARCHAR NOT NULL,
    started_at TIMESTAMP NOT NULL,
    ended_at TIMESTAMP,
    status VARCHAR NOT NULL,
    file_id BIGINT,
    FOREIGN KEY (file_id) REFERENCES binary_contents(id)
);