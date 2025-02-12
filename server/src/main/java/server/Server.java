package server;

import com.google.gson.Gson;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import requests.Request;
import service.ChessService;
import spark.Spark;
import static spark.Spark.delete;
import static spark.Spark.post;


public class Server {
    private final ChessService chessService;

    public Server(ChessService chessService){
        this.chessService = chessService;
    }

    // Default constructor uses memory instead of MySQL
    public Server(){
        AuthDAO authAccess = new MemoryAuthDAO();
        GameDAO gameAccess = new MemoryGameDAO();
        UserDAO userAccess = new MemoryUserDAO();
        this.chessService = new ChessService(authAccess, gameAccess, userAccess);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        post("/user", this::register);
        delete("/db", this::clear);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object clear(spark.Request req, spark.Response res){
        System.out.println("Clear | Handler");
        // Handle request
        Request.Delete delete = new Request.Delete();
        chessService.clear(delete);
        
        return "";
    }

    public Object register(spark.Request req, spark.Response res){
        var user = new Gson().fromJson(req.body(), Request.Register.class);
        var userRes = chessService.register(user);
        return new Gson().toJson(userRes);
    }

    

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
