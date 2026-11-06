DELIMITER //

CREATE TRIGGER tgr_user_audit_delete BEFORE DELETE ON users_auth FOR EACH ROW
BEGIN
    INSERT INTO users_audit (
        user_id,
        operation,
        old_username,
        old_password,
        old_role
    ) VALUES (
        OLD.user_id,
        'D',
        OLD.username,
        OLD.password,
        OLD.role
    );
END //

DELIMITER ;