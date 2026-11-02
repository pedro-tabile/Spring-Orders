DELIMITER //

CREATE TRIGGER tgr_user_audit_update AFTER UPDATE ON user_auth FOR EACH ROW
BEGIN
    INSERT INTO users_audit (
        user_id,
        operation,
        old_username,
        old_password,
        old_role,
        username,
        password,
        role
    ) VALUES (
        OLD.user_id,
        'U',
        OLD.username,
        OLD.password,
        OLD.role,
        NEW.username,
        NEW.password,
        NEW.role
    );
END //

DELIMITER ;