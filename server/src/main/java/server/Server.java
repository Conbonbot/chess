package server;

import spark.Request;
import spark.Response;
import spark.Spark;
import static spark.Spark.delete;

public class Server {

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

    public Object clear(Request req, Response res){
        System.out.println("This is a big test");
        return "";
    }

    

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
