# utf8mb4 fix
ALTER TABLE ofMucRoom CHANGE subject subject VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;