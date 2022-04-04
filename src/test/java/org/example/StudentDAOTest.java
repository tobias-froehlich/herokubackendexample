package org.example;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StudentDAOTest extends DAOTest {

    private StudentDAO studentDAO;

    @BeforeAll
    @Override
    public void beforeAll() {
        super.beforeAll();
        studentDAO = new StudentDAO(connection);
    }

    @Test
    public void testAddStudent() {
        Student student = new Student("John", "Doe");
        studentDAO.addStudent(student);
        List<Student> students = new ArrayList<>();
        String query = "SELECT first_name, last_name FROM student WHERE TRUE;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                students.add(new Student(
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertThat(students).hasSize(1);
        assertThat(students.get(0).getFirstName()).isEqualTo("John");
        assertThat(students.get(0).getLastName()).isEqualTo("Doe");
    }
    
}
