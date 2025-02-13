package server;

import com.google.gson.Gson;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import requests.Request;
import results.Result;
import service.ChessService;
import spark.Spark;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;


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
        post("/session", this::login);
        delete("/session", this::logout);
        get("/game", this::listGames);
        post("/game", this::createGame);
        put("/game", this::joinGame);
        delete("/db", this::clear);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object clear(spark.Request req, spark.Response res){
        // Handle request
        Request.Delete delete = new Request.Delete();
        chessService.clear(delete);
        
        return "";
    }

    public Object register(spark.Request req, spark.Response res){
        var user = new Gson().fromJson(req.body(), Request.Register.class);
        var userRes = chessService.register(user);
        if(userRes == null){
            res.status(403);
            return new Gson().toJson(new Result.Error("Error: already taken"));
        }
        return new Gson().toJson(userRes);
    }

    public Object login(spark.Request req, spark.Response res){
        var login = new Gson().fromJson(req.body(), Request.Login.class);
        var loginRes = chessService.login(login);
        if(loginRes == null){
            res.status(401);
            return new Gson().toJson(new Result.Error("Error: unauthorized"));
        }
        return new Gson().toJson(loginRes);
    }

    public Object logout(spark.Request req, spark.Response res){
        var logout = new Request.Logout(req.headers("Authorization"));
        var logoutRes = chessService.logout(logout);
        return new Gson().toJson(logoutRes);
    }

    public Object listGames(spark.Request req, spark.Response res){
        var auth = new Request.GetGames(req.headers("Authorization"));
        var listGamesRes = chessService.showGames(auth);
        return new Gson().toJson(listGamesRes);
    }

    public Object createGame(spark.Request req, spark.Response res){
        String auth = req.headers("Authorization");
        var create = new Gson().fromJson(req.body(), Request.CreateGame.class);
        var createRes = chessService.createGame(auth, create);
        if(createRes.gameID() == -1){
            return new Gson().toJson(new Result.Error("Error: unauthorized"));
        }
        return new Gson().toJson(createRes);
    }

    public Object joinGame(spark.Request req, spark.Response res){
        String auth = req.headers("Authorization");
        var join = new Gson().fromJson(req.body(), Request.JoinGame.class);
        var joinRes = chessService.joinGame(auth, join);
        return new Gson().toJson(joinRes);
    }

    

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
