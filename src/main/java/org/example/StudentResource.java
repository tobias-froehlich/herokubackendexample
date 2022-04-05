package org.example;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServlet;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

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

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Student> getAll() {
        List<Student> students = null;
        try {
            students = studentDao.getAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(students);
        return students;
    }

    @POST
    @Path("add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Student add(Student student) {
        System.out.println(student);
        return studentDao.addStudent(student);
    }

}
