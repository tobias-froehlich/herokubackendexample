package org.example;

import java.sql.Connection;

public class ExampleContext {

    //private final Connection connection;
    private final StudentDAO myDAO;

    public ExampleContext(Connection connection) {
        //this.connection = connection;
        myDAO = new StudentDAO(connection);

    }


}
