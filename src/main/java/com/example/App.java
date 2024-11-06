// package com.example;

// import java.io.IOException;
// import java.net.InetSocketAddress;
// import com.sun.net.httpserver.HttpServer;
// import com.sun.net.httpserver.HttpHandler;
// import com.sun.net.httpserver.HttpExchange;

// public class App {
//     public static void main(String[] args) throws IOException {
//         System.out.println("Hello World!");
        
//         // Set up a basic HTTP server
//         HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
//         server.createContext("/", new HttpHandler() {
//             @Override
//             public void handle(HttpExchange exchange) throws IOException {
//                 String response = "Hello from the Dockerized Java app!";
//                 exchange.sendResponseHeaders(200, response.getBytes().length);
//                 exchange.getResponseBody().write(response.getBytes());
//                 exchange.close();
//             }
//         });
//         server.setExecutor(null); // creates a default executor
//         server.start();

//         System.out.println("Server started at http://localhost:8081");
//     }
// }

package com.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class App {
    private static final String ENVIRONMENT = System.getProperty("environment", "local");
    
    public static void main(String[] args) throws IOException {
        System.out.println("Starting application in " + ENVIRONMENT + " environment");
        
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        
        // Add a health check endpoint
        server.createContext("/health", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = "OK";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
            }
        });
        
        // Main endpoint
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = String.format("Hello from the %s environment!", ENVIRONMENT);
                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
            }
        });
        
        server.setExecutor(null);
        server.start();
        System.out.println("Server started at http://localhost:8081");
    }
}