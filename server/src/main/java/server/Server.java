package server;

import com.google.gson.Gson;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.MySqlAuthDAO;
import dataaccess.MySqlGameDAO;
import dataaccess.MySqlUserDAO;
import exception.ResponseException;
import requests.Request;
import results.Result;
import service.ChessService;
import spark.Spark;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;


public class Server {
    private final ChessService chessService;

    public Server(){
        ChessService serv;
        try{
            serv = new ChessService(new MySqlAuthDAO(), new MySqlGameDAO(), new MySqlUserDAO());
        }
        catch(ResponseException ex){
            serv = new ChessService(new MemoryAuthDAO(), new MemoryGameDAO(), new MemoryUserDAO());
        }
        chessService = serv;
    }

    public Server(ChessService chessService){
        this.chessService = chessService;
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
        exception(ResponseException.class, this::exceptionHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void exceptionHandler(ResponseException ex, spark.Request req, spark.Response res){
        res.status(ex.statusCode());
        res.body(ex.toJson());
    }

    public Object clear(spark.Request req, spark.Response res) throws ResponseException{
        // Handle request
        Request.Delete delete = new Request.Delete();
        chessService.clear(delete);
        
        return "";
    }

    public Object register(spark.Request req, spark.Response res) throws ResponseException{
        var user = new Gson().fromJson(req.body(), Request.Register.class);
        var userRes = chessService.register(user);
        return new Gson().toJson(userRes);
        
    }

    public Object login(spark.Request req, spark.Response res) throws ResponseException{
        var login = new Gson().fromJson(req.body(), Request.Login.class);
        var loginRes = chessService.login(login);
        if(loginRes.authToken() == null){
            res.status(401);
            return new Gson().toJson(new Result.Error("Error: unauthorized"));
        }
        return new Gson().toJson(loginRes);
    }

    public Object logout(spark.Request req, spark.Response res) throws ResponseException{
        var logout = new Request.Logout(req.headers("Authorization"));
        chessService.logout(logout);
        return "";
    }

    public Object listGames(spark.Request req, spark.Response res) throws ResponseException{
        var auth = new Request.GetGames(req.headers("Authorization"));
        var listGamesRes = chessService.showGames(auth);
        return new Gson().toJson(listGamesRes);
    }

    public Object createGame(spark.Request req, spark.Response res) throws ResponseException{
        String auth = req.headers("Authorization");
        var create = new Gson().fromJson(req.body(), Request.CreateGame.class);
        var createRes = chessService.createGame(auth, create);
        return new Gson().toJson(createRes);
    }

    public Object joinGame(spark.Request req, spark.Response res) throws ResponseException{
        String auth = req.headers("Authorization");
        var join = new Gson().fromJson(req.body(), Request.JoinGame.class);
        chessService.joinGame(auth, join);
        return "";
        
    }

    

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
