package server;

import requests.Request;
import service.DatabaseService;
import service.GameService;
import service.UserService;
import spark.Spark;
import static spark.Spark.delete;


public class Server {
    private final DatabaseService dbService;
    private final GameService gameService;
    private final UserService userService;

    public Server(DatabaseService dbService, GameService gameService, UserService userService){
        this.dbService = dbService;
        this.gameService = gameService;
        this.userService = userService;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //delete("/db", (req, res) -> clear());
        delete("/db", this::clear);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object clear(spark.Request req, spark.Response res){
        System.out.println("clearing db");
        // Handle request
        Request.Delete delete = new Request.Delete();
        dbService.clear(delete);
        
        return "";
    }

    

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
