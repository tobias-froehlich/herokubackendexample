package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StudentDAOTest extends TestWithDB {

    private StudentDAO studentDAO;

    @BeforeAll
    @Override
    public void beforeAll() {
        super.beforeAll();
        studentDAO = new StudentDAO(dslContext);
    }

    @Test
    public void testAddStudent() {
        Student student = new Student("John", "Doe");
        studentDAO.addStudent(student);
        List<Student> students = dslContext.selectFrom("student")
                .fetch(record ->
                        new Student(
                                record.getValue("first_name", String.class),
                                record.getValue("last_name", String.class)
                        )
                );
//        List<Student> students = new ArrayList<>();
//        String query = "SELECT first_name, last_name FROM student WHERE TRUE;";
//        try (PreparedStatement statement = connection.prepareStatement(query)) {
//            ResultSet resultSet = statement.executeQuery();
//            while (resultSet.next()) {
//                students.add(new Student(
//                        resultSet.getString("first_name"),
//                        resultSet.getString("last_name")
//                ));
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        assertThat(students).hasSize(1);
        assertThat(students.get(0).getFirstName()).isEqualTo("John");
        assertThat(students.get(0).getLastName()).isEqualTo("Doe");
    }

    @Test
    public void testGetAll() {
        dslContext.insertInto(table("student"))
                .set(field("first_name"), "Aste")
                .set(field("last_name"), "Rix")
                .execute();
        dslContext.insertInto(table("student"))
                .set(field("first_name"), "Obe")
                .set(field("last_name"), "Lix")
                .execute();
        List<Student> expected = List.of(
                new Student("Aste", "Rix"),
                new Student("Obe", "Lix")
        );
        List<Student> actual = studentDAO.getAll();
        assertThat(actual).hasSize(2);
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
    
}
