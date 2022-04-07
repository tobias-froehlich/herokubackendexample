package org.example;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class UserDAO {
    private final DSLContext dslContext;

    public UserDAO(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public List<User> getAllAppUsers() {
        List<User> users = dslContext.selectFrom("user_account").fetch(new UserMapper());
        for(User user : users) {
            user.setPassword("");
        }
        return users;
    }

    public User getUser(String id, String auth) throws ApplicationException {
        System.out.println("DAO getUser auth = " + auth);
        if (auth == null) {
            throw new ApplicationException("Authentication header is missing.");
        }
        String[] words = auth.split(" ");
        if (words.length != 2) {
            throw new ApplicationException("Wrong authentication header.");
        }
        if (!"Basic".equals(words[0])) {
            throw new ApplicationException("Wrong authentication method.");
        }

        List<User> users = dslContext.selectFrom("user_account")
                .where(field("id").eq(id))
                .fetch(new UserMapper());
        if (users.size() == 0) {
            throw new ApplicationException("User with id = " + id + " not found.");
        }
        User user = users.get(0);
        String credentials = new String(Base64.getDecoder().decode(words[1]));
        System.out.println("credentials: " + credentials);
        String[] credentialWords = credentials.split(":");
        if (credentialWords.length != 2 || !credentialWords[0].equals(user.getId()) || !credentialWords[1].equals(user.getPassword())) {
            throw new ApplicationException("Wrong credentials.");
        }

        return user;
    }

    public User addUser(User user) throws ApplicationException {
        String newId = UUID.randomUUID().toString();
        String newVersion = UUID.randomUUID().toString();
        dslContext.transaction(configuration -> {
            Integer count = DSL.using(configuration)
                    .selectCount()
                    .from("user_account")
                    .where(field("name").eq(user.getName()))
                    .fetchOne(0, Integer.class);
            if (count != null && count > 0) {
                throw new ApplicationException("Cannot insert user. User with name " + user.getName() + " already exists.");
            }
            DSL.using(configuration)
                    .insertInto(table("user_account"))
                    .columns(field("id"), field("version"), field("name"), field("password"))
                    .values(newId, newVersion, user.getName(), user.getPassword())
                    .execute();
        });
        return getUser(newId, makeAuth(newId, user.getPassword()));
    }

    public User updateUser(User user, String auth) throws ApplicationException {
        System.out.println("DAO updateUser auth = " + auth);
        User authenticatedUser = getUser(user.getId(), auth);
        String newVersion = UUID.randomUUID().toString();
        dslContext.transaction(configuration -> {
            List<User> users = DSL.using(configuration)
                    .selectFrom("user_account")
                    .where(field("id").eq(user.getId()))
                    .fetch(new UserMapper());
            int count = users.size();
            if (count != 1) {
                throw new ApplicationException("User with id = " + user.getId() + " exists " + count + " times.");
            }
            if (user.getVersion() == null || !user.getVersion().equals(users.get(0).getVersion())) {
                throw new ApplicationException("User with id = " + user.getId() + " has a new version.");
            }
            DSL.using(configuration)
                    .update(table("user_account"))
                    .set(field("id"), user.getId())
                    .set(field("version"), newVersion)
                    .set(field("name"), user.getName())
                    .set(field("password"), user.getPassword())
                    .where(field("id").eq(user.getId()))
                    .execute();
        });
        return getUser(user.getId(), makeAuth(user.getId(), user.getPassword()));
    }

    public User deleteUser(User user, String auth) {
        User authenticatedUser = getUser(user.getId(), auth);
        dslContext.deleteFrom(table("user_account"))
                .where(field("id").eq(user.getId()))
                .execute();
        return authenticatedUser;
    }

    private String makeAuth(String id, String password) {
        return "Basic " + new String(Base64.getEncoder().encode((id + ":" + password).getBytes(StandardCharsets.UTF_8)));
    }


    public static class UserMapper implements RecordMapper<Record, User> {
        @Override
        public User map(Record record) {
            return new User(
                    record.getValue("id", String.class),
                    record.getValue("version", String.class),
                    record.getValue("name", String.class),
                    record.getValue("password", String.class)
            );
        }
    }

}
