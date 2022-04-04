package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DAOTest {

    protected Connection connection;

    @BeforeAll
    public void beforeAll() {
        try {
            connection = DriverManager.getConnection("jdbc:h2:memFS:test", "sa", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Migrator migrator = new Migrator(connection);
        migrator.migrate();
    }

    @BeforeEach
    public void beforeEach() {
        String clearQueryAppUser = "TRUNCATE TABLE app_user;";
        String clearQueryStudent = "TRUNCATE TABLE student;";
        try (PreparedStatement clearStatementAppUser = connection.prepareStatement(clearQueryAppUser);
             PreparedStatement clearStatementStudent = connection.prepareStatement(clearQueryStudent)) {
            clearStatementAppUser.execute();
            clearStatementStudent.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
