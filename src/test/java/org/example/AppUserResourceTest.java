package org.example;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.internal.monitoring.RequestEventBuilder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppUserResourceTest extends TestWithDB {

    private ServletHolder servletHolder;
    private ServletContainer servletContainer;
    private ServletContextHandler handler;


//    @BeforeAll
//    @Override
//    public void beforeAll() {
//        super.beforeAll();
//        final StudentDAO studentDao = new StudentDAO(dslContext);
//        final StudentResource studentResource = new StudentResource(studentDao);
//        final AppUserDAO appUserDAO = new AppUserDAO(dslContext);
//        final AppUserResource appUserResource = new AppUserResource(appUserDAO, new Migrator(dslContext));
//
//        ResourceConfig resourceConfig = new ResourceConfig();
//        Set<Object> instances = new HashSet<>();
//        instances.add(studentResource);
//        instances.add(appUserResource);
//        resourceConfig.registerInstances(instances);
//        resourceConfig.register(JacksonFeature.class);
//        ServletContainer servletContainer = new ServletContainer(resourceConfig);
//        ServletHolder servletHolder = new ServletHolder(servletContainer);
//        try {
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////        try {
////            this.servletContainer.init();
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//        this.servletHolder = servletHolder;
//        this.servletContainer = servletContainer;
//        ServletContextHandler handler = new ServletContextHandler();
//        handler.addServlet(servletHolder, "/*");
//        this.handler = handler;
//    }
//
//    @Test
//    public void testGetAllUsers() {
//        HttpServletRequest req = new RequestBuilder().build();
//        HttpServletResponse res = new ResponseBuilder().build();
//
//        try {
//            System.out.println("hello");
//            servletHolder.handle(Request.getBaseRequest(req), req, res);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


}
