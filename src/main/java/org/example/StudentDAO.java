package org.example;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class StudentDAO {
    private final DSLContext dslContext;

    public StudentDAO(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public List<Integer> getMigrations() {
        List<Integer> numbers = dslContext
                .select(field("number"))
                .from("migration")
                .fetch(record -> record.getValue("number", int.class));
        return numbers;
//        String query = "SELECT number FROM migration;";
//        try (PreparedStatement statement = connection.prepareStatement(query)) {
//            ResultSet resultSet = statement.executeQuery();
//            List<Integer> result = new ArrayList<>();
//            while (resultSet.next()) {
//                result.add(resultSet.getInt(1));
//            }
//            return result;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    public List<Student> getAll() {
        return dslContext.selectFrom("student")
                .fetch(record -> new Student(record.getValue("first_name", String.class), record.getValue("last_name", String.class)));
    }

    public Student addStudent(Student student) {
        dslContext.insertInto(table("student"))
                .columns(field("first_name"), field("last_name"))
                .values(student.getFirstName(), student.getLastName())
                .execute();
        return student;
//        String query = "INSERT INTO student (first_name, last_name) VALUES (?, ?)";
//        try (PreparedStatement statement = connection.prepareStatement(query)) {
//            statement.setString(1, student.getFirstName());
//            statement.setString(2, student.getLastName());
//            statement.execute();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return student;
    }

}
