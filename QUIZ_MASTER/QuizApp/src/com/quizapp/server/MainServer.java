package com.quizapp.server;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class MainServer {
    public static void main(String[] args) throws Exception {
        // Initialize DB
        DatabaseManager.getInstance();

        int port = 8080;
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector(); // trigger initialization 

        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());
        Tomcat.addServlet(ctx, "QuizServlet", new QuizServlet());
        ctx.addServletMappingDecoded("/api/*", "QuizServlet");

        System.out.println("Starting Tomcat Embedded Server on port " + port + "...");
        tomcat.start();
        tomcat.getServer().await();
    }
}
