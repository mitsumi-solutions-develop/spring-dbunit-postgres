SELECT pg_catalog.setval(pg_get_serial_sequence('tbl_user', 'user_id'), (SELECT MAX(user_id) FROM tbl_user) + 1);
