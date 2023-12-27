--DROP SCHEMA public CASCADE;
--CREATE SCHEMA public;

CREATE TABLE IF NOT EXISTS users (
  user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  email VARCHAR NOT NULL UNIQUE,
  name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS item_requests (
  item_request_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  requesting_user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
  description VARCHAR NOT NULL,
  created TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
  item_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR NOT NULL,
  description VARCHAR NOT NULL,
  owner_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
  is_available BOOLEAN NOT NULL,
  item_request_id BIGINT REFERENCES item_requests(item_request_id)
);

CREATE TABLE IF NOT EXISTS bookings (
  booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  item_id BIGINT NOT NULL REFERENCES items(item_id),
  booker_id BIGINT NOT NULL REFERENCES users(user_id),
  start_date_time TIMESTAMP NOT NULL,
  end_date_time TIMESTAMP NOT NULL,
  status VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS comments (
  comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  item_id BIGINT NOT NULL REFERENCES items(item_id),
  author_id BIGINT NOT NULL REFERENCES users(user_id),
  created TIMESTAMP NOT NULL,
  text VARCHAR NOT NULL
);