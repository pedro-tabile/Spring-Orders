DELIMITER //

CREATE TRIGGER tgr_user_audit_insert AFTER INSERT ON user_auth FOR EACH ROW
BEGIN
    INSERT INTO users_audit (
        user_id,
        operation,
        username,
        password,
        role
    ) VALUES (
        NEW.user_id,
        'I',
        NEW.username,
        NEW.password,
        NEW.role
    );
END //

DELIMITER ;