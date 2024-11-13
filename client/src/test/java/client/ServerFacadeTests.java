package client;

import network.ServerFacade;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        ServerFacade facade = new ServerFacade();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void register() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertTrue(authData.authToken().length() > 10);
    }

}
