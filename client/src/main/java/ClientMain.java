
//import chess.ChessGame;

import chessclient.ChessClient;

public class ClientMain {
    public static void main(String[] args) throws Exception{
        ChessClient facade = new ChessClient(3000);
        facade.run();
    }
}