package org.example;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServlet;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("student")
public class StudentResource {

    final StudentDAO studentDao;

    public StudentResource(StudentDAO studentDao) {
        this.studentDao = studentDao;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getHello() {
        return "hello world from resource";
    }


}
