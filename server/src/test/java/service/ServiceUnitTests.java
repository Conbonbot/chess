package service;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Order;
 import org.junit.jupiter.api.Test;

import server.Server;

/**
 *
 * @author Connor
 */
public class ServiceUnitTests{

    private static Server server;

    @AfterAll
    static void stopServer(){
        server.stop();
    }

    @BeforeAll
    public static void init(){
        server = new Server();
        var port = server.run(3000);
        System.out.println("Started test HTTP server on " + port);
        // Add init for users
    }

    
    @Test
    @Order(1)
    @DisplayName("Clear Service")
    public void clearService() {
        
    }

}
