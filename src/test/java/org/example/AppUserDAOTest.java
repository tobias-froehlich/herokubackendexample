package org.example;

import org.jooq.SelectQualifyConditionStep;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.setRemoveAssertJRelatedElementsFromStackTrace;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppUserDAOTest extends DAOTest{

    private AppUserDAO userDAO;

    @BeforeAll
    @Override
    public void beforeAll() {
        super.beforeAll();
        userDAO = new AppUserDAO(connection);
    }

    @BeforeEach
    @Override
    public void beforeEach() {
        super.beforeEach();
    }

    @Test
    public void testGetAppUser() {
        String query = "INSERT INTO app_user (id, version, name, password) VALUES(?, ?, ?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "012345678901234567890123456789012345");
            statement.setString(2, "test-version");
            statement.setString(3, "John");
            statement.setString(4, "johns-password");
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        AppUser user = userDAO.getAppUser("012345678901234567890123456789012345");
        assertThat(user.getId()).isEqualTo("012345678901234567890123456789012345");
        assertThat(user.getVersion()).isEqualTo("test-version");
        assertThat(user.getName()).isEqualTo("John");
        assertThat(user.getPassword()).isEqualTo("johns-password");
    }

    @Test
    public void testAddAppUser() {
        AppUser user = new AppUser(null, null, "John", "johns-password");
        AppUser addedUser = userDAO.addAppUser(user);
        assertThat(addedUser.getName()).isEqualTo("John");
        assertThat(addedUser.getPassword()).isEqualTo("johns-password");
    }
}
