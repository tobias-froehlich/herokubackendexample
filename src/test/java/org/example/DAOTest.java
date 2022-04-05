package org.example;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
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

    protected DSLContext dslContext;

    @BeforeAll
    public void beforeAll() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:h2:memFS:test", "sa", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dslContext = DSL.using(connection, SQLDialect.POSTGRES);
        Migrator migrator = new Migrator(dslContext);
        migrator.migrate();
    }

    @BeforeEach
    public void beforeEach() {
        dslContext.truncateTable("APP_USER").execute();
        dslContext.truncateTable("STUDENT").execute();
//        String clearQueryAppUser = "TRUNCATE TABLE app_user;";
//        String clearQueryStudent = "TRUNCATE TABLE student;";
//        try (PreparedStatement clearStatementAppUser = connection.prepareStatement(clearQueryAppUser);
//             PreparedStatement clearStatementStudent = connection.prepareStatement(clearQueryStudent)) {
//            clearStatementAppUser.execute();
//            clearStatementStudent.execute();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
}
