DELIMITER $
CREATE TRIGGER tgr_user_audit_insert AFTER UPDATE ON employees FOR EACH ROW
BEGIN
    INSERT INTO employees_audit (
        employee_id,
        name,
        old_name,
        salary,
        old_salary,
        birthday,
        old_birthday,
        operation
    ) VALUES (
        NEW.id,
        NEW.name,
        OLD.name,
        NEW.salary,
        OLD.salary,
        NEW.birthday,
        OLD.birthday,
        'U'
    );
END $