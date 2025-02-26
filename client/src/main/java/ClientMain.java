
//import chess.ChessGame;

import serverfacade.ServerFacade;

public class ClientMain {
    public static void main(String[] args) throws Exception{
        ServerFacade facade = new ServerFacade(3000);
        facade.run();
    }
}