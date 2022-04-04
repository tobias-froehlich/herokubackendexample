package org.example;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.QOM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.SQLDataType.INTEGER;
import static org.jooq.impl.SQLDataType.VARCHAR;

public class Migrator {
    private final Connection connection;
    private final DSLContext dslContext;
    private final List<MigrationStep> migrationSteps;
    private boolean migrationStopped;

    public Migrator(Connection connection) {
        this.migrationStopped = false;
        this.connection = connection;
        this.dslContext = DSL.using(connection, SQLDialect.POSTGRES);
        migrationSteps = new ArrayList<>();
        migrationSteps.add(new MigrationStep("Creating student table.", dslContext -> {
                dslContext.createTable("student")
                        .column("fist_name", VARCHAR(32))
                        .column("last_name", VARCHAR(32)).execute();
        }));
        migrationSteps.add(new MigrationStep("Correcting column name.", dslContext -> {
            dslContext
                    .alterTable("student")
                    .renameColumn("fist_name")
                    .to("first_name")
                    .execute();
//            String query = "ALTER TABLE student RENAME COLUMN fist_name TO first_name;";
//            try (PreparedStatement statement = con.prepareStatement(query)) {
//                statement.execute();
//                return true;
//            } catch (SQLException e) {
//                e.printStackTrace();
//                return false;
//            }
        }));
        migrationSteps.add(new MigrationStep("Creating app_user table.", dslContext -> {
            dslContext
                    .createTableIfNotExists("app_user")
                    .column("id", VARCHAR(36))
                    .column("version", VARCHAR(36))
                    .column("name", VARCHAR(36))
                    .column("password", VARCHAR(36))
                    .execute();
//            String query = "CREATE TABLE IF NOT EXISTS app_user ();";
//            try (PreparedStatement statement = con.prepareStatement(query)) {
//                statement.execute();
//                return true;
//            } catch (SQLException e) {
//                e.printStackTrace();
//                return false;
//            }
        }));
//        migrationSteps.add(new MigrationStep("Adding columns to app_user table.", con -> {
//            String query1 = "ALTER TABLE app_user ADD COLUMN id VARCHAR(36);";
//            String query2 = "ALTER TABLE app_user ADD COLUMN version VARCHAR(36);";
//            String query3 = "ALTER TABLE app_user ADD COLUMN name VARCHAR(36);";
//            String query4 = "ALTER TABLE app_user ADD COLUMN password VARCHAR(36);";
//            try (PreparedStatement statement1 = con.prepareStatement(query1);
//                 PreparedStatement statement2 = con.prepareStatement(query2);
//                 PreparedStatement statement3 = con.prepareStatement(query3);
//                 PreparedStatement statement4 = con.prepareStatement(query4);) {
//                statement1.execute();
//                statement2.execute();
//                statement3.execute();
//                statement4.execute();
//                return true;
//            } catch (SQLException e) {
//                e.printStackTrace();
//                return false;
//            }
//        }));
        migrationSteps.add(new MigrationStep("Adding Primary key to app_user table.", dslContext -> {
            dslContext.alterTable("app_user")
                    .alterColumn("id")
                    .setNotNull()
                    .execute();
            dslContext.alterTable("app_user")
                    .add(
                            DSL.constraint("pk_app_user").primaryKey("id")
                    ).execute();
//            String query1 = "ALTER TABLE app_user ALTER COLUMN id SET NOT NULL;";
//            String query2 = "ALTER TABLE app_user ADD PRIMARY KEY (id);";
//            try (PreparedStatement statement1 = con.prepareStatement(query1);
//                 PreparedStatement statement2 = con.prepareStatement(query2)) {
//                statement1.execute();
//                statement2.execute();
//                return true;
//            } catch (SQLException e) {
//                e.printStackTrace();
//                return false;
//            }
        }));
        migrationSteps.add(new MigrationStep("Adding Unique constraint to app_user name.", dslContext -> {
            dslContext.alterTable("app_user")
                    .add(
                            DSL.constraint("unique_constraint_app_user_name").unique("name")
                    ).execute();
//            String query = "ALTER TABLE app_user ADD CONSTRAINT constraint_app_user_name_unique UNIQUE (name);";
//            try (PreparedStatement statement = con.prepareStatement(query)) {
//                statement.execute();
//                return true;
//            } catch (SQLException e){
//                e.printStackTrace();
//                return false;
//            }
        }));
    }

    public void migrate() {
        String query = "CREATE TABLE IF NOT EXISTS migration (number integer);";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < migrationSteps.size(); i++) {
            Integer count = dslContext
                    .selectCount()
                    .from("migration")
                    .where(field("number").eq(i))
                    .fetchOne(0, Integer.class);
            boolean carriedOut = count != null && count > 0;

//            String checkQuery = "SELECT number FROM migration WHERE number=?;";
//            boolean carriedOut = false;
//            try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
//                statement.setInt(1, i);
//                ResultSet resultSet = statement.executeQuery();
//                carriedOut = resultSet.next();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
            if (carriedOut) {
                System.out.println("Migration #" + i + " (" + migrationSteps.get(i).getDescription() + ") was carried out already. Skip it.");
            } else {
                System.out.println("Migration #" + i + " (" + migrationSteps.get(i).getDescription() + ") was not carried out. Perform migration.");
                try {
                    migrationSteps.get(i).getCallback().performStep(dslContext);
                } catch (DataAccessException e) {
                    e.printStackTrace();
                    migrationStopped = true;
                    System.out.println("Migration stopped because step #" + i + " failed.");
                }

            }
            if (migrationStopped) {
                break;
            }
            if (!carriedOut) {
                dslContext.insertInto(table("migration"), field("number"))
                        .values(i)
                        .execute();
//                String updateMigrationQuery = "INSERT INTO migration (number) VALUES (?);";
//                try (PreparedStatement statement = connection.prepareStatement(updateMigrationQuery)) {
//                    statement.setInt(1, i);
//                    statement.execute();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

    private static class MigrationStep {
        private final String description;
        private final MigrationStepCallback callback;

        public MigrationStep(String description, MigrationStepCallback callback) {
            this.description = description;
            this.callback = callback;
        }

        public String getDescription() {
            return description;
        }

        public MigrationStepCallback getCallback() {
            return callback;
        }
    }

    private static interface MigrationStepCallback {
        void performStep(DSLContext dslContext) throws DataAccessException;
    }
}
