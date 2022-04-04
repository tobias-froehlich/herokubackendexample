package org.example;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class MyConfig extends ResourceConfig {

    public MyConfig() {
        register(StudentResource.class);
    }

}
