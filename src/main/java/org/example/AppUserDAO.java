package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AppUserDAO {
    private final Connection connection;

    public AppUserDAO(Connection connection) {
        this.connection = connection;
    }

    public AppUser getAppUser(String id) throws DatabaseConflictException {
        String query = "SELECT id, version, name, password FROM app_user WHERE id=?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new AppUser(
                        resultSet.getString("id"),
                        resultSet.getString("version"),
                        resultSet.getString("name"),
                        resultSet.getString("password")
                );
            } else {
                throw new DatabaseConflictException("User with id = " + id + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public AppUser addAppUser(AppUser user) throws DatabaseConflictException {
        String newId = UUID.randomUUID().toString();
        String newVersion = UUID.randomUUID().toString();
        String begin = "BEGIN;";
        String query1 = "SELECT id FROM app_user WHERE id=?;";
        String query2 = "INSERT INTO app_user (id, version, name, password) VALUES(?, ?, ?, ?);";
        String commit = "COMMIT;";
        try (PreparedStatement statementBegin = connection.prepareStatement(begin);
             PreparedStatement statement1 = connection.prepareStatement(query1);
             PreparedStatement statement2 = connection.prepareStatement(query2);
             PreparedStatement statementCommit = connection.prepareStatement(commit)) {
            statementBegin.execute();
            statement1.setString(1, user.getId());
            ResultSet resultSet = statement1.executeQuery();
            if (resultSet.next()) {
                throw new DatabaseConflictException("Cannot insert user. User with id " + user.getId() + " already exists.");
            }
            System.out.println(newId);
            statement2.setString(1, newId);
            statement2.setString(2, newVersion);
            statement2.setString(3, user.getName());
            statement2.setString(4, user.getPassword());
            statement2.execute();
            statementCommit.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getAppUser(newId);
    }

    public AppUser updateAppUser(AppUser user) throws DatabaseConflictException {
        String begin = "BEGIN;";
        String query1 = "SELECT id, version, name, password FROM app_user WHERE id=?";
        String query2 = "UPDATE user SET (id, version, name, password)=(?, ?, ?, ?);";
        String commit = "COMMIT;";
        try (PreparedStatement statementBegin = connection.prepareStatement(begin);
             PreparedStatement statement1 = connection.prepareStatement(query1);
             PreparedStatement statement2 = connection.prepareStatement(query2);
             PreparedStatement statementCommit = connection.prepareStatement(commit)) {
            statementBegin.execute();
            statement1.setString(1, user.getId());
            ResultSet resultSet = statement1.executeQuery();
            int count = 0;
            while(resultSet.next()) {
                count++;
                String version = resultSet.getString("version");
                if (!user.getVersion().equals(version)) {
                    throw new DatabaseConflictException("User with id = " + user.getId() + " cannot be edited because the version is outdated.");
                }

            }
            if (count != 1) {
                throw new DatabaseConflictException("User with id = " + user.getId() + " exists " + count + " times.");
            }
            String newVersion = UUID.randomUUID().toString();
            statement2.setString(1, user.getId());
            statement2.setString(2, newVersion);
            statement2.setString(3, user.getName());
            statement2.setString(4, user.getPassword());
            statement2.execute();
            statementCommit.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getAppUser(user.getId());
    }



}
