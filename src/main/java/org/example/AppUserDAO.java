package org.example;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class AppUserDAO {
    private final DSLContext dslContext;

    public AppUserDAO(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public AppUser getAppUser(String id) throws DatabaseConflictException {
        List<AppUser> appUsers = dslContext.selectFrom("app_user").fetch(new AppUserMapper());
        if (appUsers.size() == 0) {
            throw new DatabaseConflictException("User with id = " + id + " not found.");
        }
        return appUsers.get(0);
//        String query = "SELECT id, version, name, password FROM app_user WHERE id=?;";
//        try (PreparedStatement statement = connection.prepareStatement(query)) {
//            statement.setString(1, id);
//            ResultSet resultSet = statement.executeQuery();
//            if (resultSet.next()) {
//                return new AppUser(
//                        resultSet.getString("id"),
//                        resultSet.getString("version"),
//                        resultSet.getString("name"),
//                        resultSet.getString("password")
//                );
//            } else {
//                throw new DatabaseConflictException("User with id = " + id + " not found.");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    public AppUser addAppUser(AppUser user) throws DatabaseConflictException {
        String newId = UUID.randomUUID().toString();
        String newVersion = UUID.randomUUID().toString();
        dslContext.transaction(configuration -> {
            Integer count = DSL.using(configuration)
                    .selectCount()
                    .from("upp_user")
                    .where(field("id").eq(user.getId()))
                    .fetchOne(0, Integer.class);
            if (count != null && count > 0) {
                throw new DatabaseConflictException("Cannot insert user. User with id " + user.getId() + " already exists.");
            }
            DSL.using(configuration)
                    .insertInto(table("app_user"))
                    .columns(field("id"), field("version"), field("name"), field("password"))
                    .values(newId, newVersion, user.getName(), user.getPassword())
                    .execute();
        });
        return getAppUser(newId);
//        String newId = UUID.randomUUID().toString();
//        String newVersion = UUID.randomUUID().toString();
//        String begin = "BEGIN;";
//        String query1 = "SELECT id FROM app_user WHERE id=?;";
//        String query2 = "INSERT INTO app_user (id, version, name, password) VALUES(?, ?, ?, ?);";
//        String commit = "COMMIT;";
//        try (PreparedStatement statementBegin = connection.prepareStatement(begin);
//             PreparedStatement statement1 = connection.prepareStatement(query1);
//             PreparedStatement statement2 = connection.prepareStatement(query2);
//             PreparedStatement statementCommit = connection.prepareStatement(commit)) {
//            statementBegin.execute();
//            statement1.setString(1, user.getId());
//            ResultSet resultSet = statement1.executeQuery();
//            if (resultSet.next()) {
//                throw new DatabaseConflictException("Cannot insert user. User with id " + user.getId() + " already exists.");
//            }
//            System.out.println(newId);
//            statement2.setString(1, newId);
//            statement2.setString(2, newVersion);
//            statement2.setString(3, user.getName());
//            statement2.setString(4, user.getPassword());
//            statement2.execute();
//            statementCommit.execute();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return getAppUser(newId);
    }

    public AppUser updateAppUser(AppUser user) throws DatabaseConflictException {
        String newVersion = UUID.randomUUID().toString();
        dslContext.transaction(configuration -> {
            List<AppUser> users = DSL.using(configuration)
                    .selectFrom("app_user")
                    .fetch(new AppUserMapper());
            int count = users.size();
            if (count != 1) {
                throw new DatabaseConflictException("User with id = " + user.getId() + " exists " + count + " times.");
            }
            DSL.using(configuration)
                    .update(table("app_user"))
                    .set(field("id"), user.getId())
                    .set(field("version"), newVersion)
                    .set(field("name"), user.getName())
                    .set(field("password"), user.getPassword())
                    .execute();
        });
        return getAppUser(user.getId());
//        String begin = "BEGIN;";
//        String query1 = "SELECT id, version, name, password FROM app_user WHERE id=?";
//        String query2 = "UPDATE user SET (id, version, name, password)=(?, ?, ?, ?);";
//        String commit = "COMMIT;";
//        try (PreparedStatement statementBegin = connection.prepareStatement(begin);
//             PreparedStatement statement1 = connection.prepareStatement(query1);
//             PreparedStatement statement2 = connection.prepareStatement(query2);
//             PreparedStatement statementCommit = connection.prepareStatement(commit)) {
//            statementBegin.execute();
//            statement1.setString(1, user.getId());
//            ResultSet resultSet = statement1.executeQuery();
//            int count = 0;
//            while(resultSet.next()) {
//                count++;
//                String version = resultSet.getString("version");
//                if (!user.getVersion().equals(version)) {
//                    throw new DatabaseConflictException("User with id = " + user.getId() + " cannot be edited because the version is outdated.");
//                }
//
//            }
//            if (count != 1) {
//                throw new DatabaseConflictException("User with id = " + user.getId() + " exists " + count + " times.");
//            }
//            String newVersion = UUID.randomUUID().toString();
//            statement2.setString(1, user.getId());
//            statement2.setString(2, newVersion);
//            statement2.setString(3, user.getName());
//            statement2.setString(4, user.getPassword());
//            statement2.execute();
//            statementCommit.execute();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return getAppUser(user.getId());
    }

    private static class AppUserMapper implements RecordMapper<Record, AppUser> {
        @Override
        public AppUser map(Record record) {
            return new AppUser(
                    record.getValue("id", String.class),
                    record.getValue("version", String.class),
                    record.getValue("name", String.class),
                    record.getValue("password", String.class)
            );
        }
    }

}
