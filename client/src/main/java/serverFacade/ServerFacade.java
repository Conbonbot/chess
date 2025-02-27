package serverfacade;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;

import com.google.gson.Gson;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import model.GameData;
import ui.EscapeSequences;

public class ServerFacade {

    private Scanner scanner;
    private String status = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "[LOGGED_OUT]" + EscapeSequences.FULL_COLOR_RESET;
    private boolean console = true;
    private final String url;
    private String authToken = "";
    private String userGameID = "";
    private String username = "";

    private void init(){
        System.out.printf("%s%s Welcome to 240 Chess. Type Help to get started %s%n", 
                        EscapeSequences.FULL_COLOR_RESET,
                        EscapeSequences.BLACK_KING, 
                        EscapeSequences.WHITE_KING);
        

        scanner = new Scanner(System.in);
    }

    public ServerFacade(int port){
        url = "http://localhost:" + port;
        scanner = new Scanner(System.in);
    }

    public void run(){
        init();
        String line = "";
        while (!line.toLowerCase().equals("quit")) {
            System.out.printf("%s >>> ", status);
            if(console && scanner.hasNextLine()) {
                line = scanner.nextLine();
                try{
                    switch(line.split(" ")[0]) {
                        // Commands
                        case "quit" -> quit();
                        case "help" -> help();
                        // Prelogin commands
                        case "login" -> login(line);
                        case "register" -> register(line);
                        // Postlogin commands
                        case "logout" -> logout();
                        case "create" -> createGame(line);
                        case "list" -> listGames();
                        case "observe" -> observeGame(line);
                        case "join" -> joinGame(line);
                        case "leave" -> leaveGame();
                        case "resign" -> resign();
                        case "move" -> makeMove(line);
                        case "legal" -> highlightMoves(line);
                        case "redraw" -> redrawBoard(line);
                        default -> System.out.printf("%s'%s' is not recognized as a command. Type help for a list%s%n",
                                    EscapeSequences.SET_TEXT_COLOR_RED, 
                                    line,
                                    EscapeSequences.RESET_TEXT_COLOR);
                    }
                }
                catch(IOException ex){
                    exceptionHandler(ex);
                }
                catch(NumberFormatException ex){
                    System.out.printf("%sYou must input a number, not a string.%s%n", 
                        EscapeSequences.SET_TEXT_COLOR_RED, 
                        EscapeSequences.FULL_COLOR_RESET);
                }
                catch(Exception ex){
                    exceptionHandler(ex);
                }
            }
            else{
                System.out.printf("%nGoodbye%n");
                scanner.close();
                break;
            }
        }
    }

    private  void exceptionHandler(IOException ex){
        try{
            switch (Integer.parseInt(ex.getMessage())) {
                case 400 -> System.out.printf("%sError: bad request%s%n", 
                            EscapeSequences.SET_TEXT_COLOR_RED, 
                            EscapeSequences.FULL_COLOR_RESET);
                case 401 -> System.out.printf("%sYou are unauthorized.%s%n", 
                            EscapeSequences.SET_TEXT_COLOR_RED, 
                            EscapeSequences.FULL_COLOR_RESET);
                case 403 -> System.out.printf("%sThis has already been taken.%s%n", 
                            EscapeSequences.SET_TEXT_COLOR_RED, 
                            EscapeSequences.FULL_COLOR_RESET);
                case 500 -> System.out.printf("%sInternal service error.%s%n", 
                            EscapeSequences.SET_TEXT_COLOR_RED, 
                            EscapeSequences.FULL_COLOR_RESET);
                default -> System.out.printf("%sAn error has occured. Try again.%s%n", 
                            EscapeSequences.SET_TEXT_COLOR_RED, 
                            EscapeSequences.FULL_COLOR_RESET);
            }
        }
        catch(NumberFormatException error){
            System.out.printf("Below is the errror%n%s%n", error.toString());
        }
    }

    // Exceptions and checkers

    private void exceptionHandler(Exception ex){
        System.out.printf("%s%s%s%n", EscapeSequences.SET_TEXT_COLOR_RED, ex.getMessage(), EscapeSequences.FULL_COLOR_RESET);
    }

    private void checkLogin() throws Exception{
        if(username.equals("")){
            throw new Exception("You must log in to use that command.\n");
        }
    }

    private void checkLength(String line, int amount) throws Exception{
        if(line.split(" ").length != amount){
            throw new Exception("The format is incorrect, use the command 'help' to show correct format");
        }
    }

    private void checkLength(String line, int lower, int upper) throws Exception{
        if(line.split(" ").length > upper || line.split(" ").length < lower){
            throw new Exception("The format is incorrect, use the command 'help' to show correct format");
        }
    }

    // Prelogin

    private void help(){
        if(username.equals("")){
            System.out.printf("\t%screate <NAME> %s- create a game%s%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                    EscapeSequences.FULL_COLOR_RESET);
            System.out.printf("\t%slist %s- list current games%s%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                    EscapeSequences.FULL_COLOR_RESET);
            System.out.printf("\t%sjoin <ID> [%sWHITE%s|%sBLACK%s] %s- join a game%s%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_WHITE,
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_BLACK, 
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                    EscapeSequences.FULL_COLOR_RESET);
            System.out.printf("\t%sobserve <ID> %s- observe a game%s%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                    EscapeSequences.FULL_COLOR_RESET);
            System.out.printf("\t%slogout %s- logout of your current acccount%s%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                    EscapeSequences.FULL_COLOR_RESET);
            System.out.printf("\t%shelp %s- show possible commands%s%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                    EscapeSequences.FULL_COLOR_RESET);
        }
        else{
            System.out.printf("\t%sregister <USERNAME> <PASSWORD> <EMAIL> %s- to create an account%s%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                    EscapeSequences.FULL_COLOR_RESET);
            System.out.printf("\t%slogin <USERNAME> <PASSWORD> %s- to play chess%s%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                    EscapeSequences.FULL_COLOR_RESET);
            System.out.printf("\t%squit %s- quit playing chess %s%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                    EscapeSequences.FULL_COLOR_RESET);
            System.out.printf("\t%shelp %s- show possible commands%s%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                    EscapeSequences.FULL_COLOR_RESET);
        }
        
    }

    public void quit(){
        System.out.println("Sounds good, have a great day");
        scanner.close();
        console = false;
        
    }

    public void login(String line) throws Exception{
        checkLength(line, 3);
        var values = line.split(" ");
        var body = Map.of("username", values[1], "password", values[2]);
        HttpURLConnection http = sendRequest(url + "/session", "POST", new Gson().toJson(body));
        String response = receiveResponse(http).toString();
        authToken = response.substring(response.indexOf("authToken")+10, response.length()-1);
        username = values[1];
        status = EscapeSequences.SET_TEXT_COLOR_BLUE + "[LOGGED_IN]" + EscapeSequences.FULL_COLOR_RESET;
        System.out.printf("Welcome back %s!%n", values[1]);
    }

    public void register(String line) throws Exception{
        checkLength(line, 4);
        var values = line.split(" ");
        var body = Map.of("username", values[1], "password", values[2], "email", values[3]);
        HttpURLConnection http = sendRequest(url + "/user", "POST", new Gson().toJson(body));
        String response = receiveResponse(http).toString();
        authToken = response.substring(response.indexOf("authToken")+10, response.length()-1);
        username = values[1];
        status = EscapeSequences.SET_TEXT_COLOR_BLUE + "[LOGGED_IN]" + EscapeSequences.FULL_COLOR_RESET;
        System.out.printf("Welcome to chess! use the command 'help' to show commands!%n");
    }

    // Postlogin

    public void logout() throws Exception{
        checkLogin();
        HttpURLConnection http = sendRequest(url + "/session", "DELETE", "", authToken);
        receiveResponse(http);
        username = "";
        status = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "[LOGGED_OUT]" + EscapeSequences.FULL_COLOR_RESET;
    }

    public void createGame(String line) throws Exception{
        checkLogin();
        checkLength(line, 2);
        String gameName = line.split(" ")[1];
        var body = Map.of("gameName", gameName);
        HttpURLConnection http = sendRequest(url + "/game", "POST", new Gson().toJson(body), authToken);
        receiveResponse(http);
        System.out.printf("The game '%s' has been created!%n", gameName);

    }

    public void listGames() throws Exception{
        checkLogin();
        HttpURLConnection http = sendRequest(url + "/game", "GET", "", authToken);
        var games = receiveResponse(http);
        ArrayList<GameData> gamesList = gamesAsList(games);
        if(gamesList.isEmpty()){
            System.out.printf("There are currently no active games%nUse the command '%screate <NAME>%s' to start one!%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE,
                    EscapeSequences.FULL_COLOR_RESET);
            return;
        }
        System.out.printf("Below are the current games\n");
        for(int i = 0; i < gamesList.size(); i++){
            System.out.printf("ID: %d | game: %s | white: %s | black: %s\n", gamesList.get(i).gameID(), gamesList.get(i).gameName(),
                        gamesList.get(i).whiteUsername(), gamesList.get(i).blackUsername());
        }
    }

    public void joinGame(String line) throws NumberFormatException, Exception{
        checkLogin();
        checkLength(line,3);
        var values = line.split(" ");
        int gameID = Integer.parseInt(values[1]);
        if(gameID < 1){
            throw new Exception("ID must be greater than 0");
        }
        if(values[2].equals("WHITE") || values[2].equals("BLACK")){
            var body = Map.of("playerColor", values[2], "gameID", gameID);
            HttpURLConnection http = sendRequest(url + "/game", "PUT", new Gson().toJson(body), authToken);
            receiveResponse(http);
            System.out.printf("Congrats on joining a game%nBelow is the board%n");
            http = sendRequest(url + "/game", "GET", "", authToken);
            var result = new Gson().fromJson(receiveResponse(http).toString(), Map.class);
            GameData game = findGame(result, values[1]);
            userGameID = values[1];
            printBoard(game.game().getBoard(), values[2].equals("WHITE"));
            System.out.printf("^ This is your side ^%n");
        }
        else{
            throw new Exception("type must be either 'WHITE' OR 'BLACK'");
        }
    }

    public void leaveGame() throws Exception{
        // TODO: implement
        checkLogin();
        if(userGameID.equals("")){
            throw new Exception("You are not currently connected to any games");
        }

    }

    public void resign() throws Exception{
        // TODO: Implement third
    }

    public void makeMove(String line) throws Exception{
        // TODO: Implement last
    }

    public void highlightMoves(String line) throws Exception{
        // TODO: Implement fourth
    }

    public void redrawBoard(String line) throws Exception{
        checkLogin();
        checkLength(line, 1, 2);
        ArrayList<GameData> userGames = userGamesAsList();
        if(userGames.isEmpty()){
            throw new Exception("You are currently not in any games");
        }
        if(userGames.size() > 1 && line.split(" ").length == 1){
            ArrayList<Integer> ids = new ArrayList<>();
            for(GameData game : userGames){
                ids.add(game.gameID());
            }
            String error = "You are currently in " + userGames.size() + " games, add an id from below to redraw the correct board.\nGameID: ";
            for(Integer id : ids){
                error += id + " ";
            }
            throw new Exception(error);
        }
        if(userGames.size() == 1){
            GameData game = userGames.get(0);
            if(game.whiteUsername() != null){
                printBoard(game.game().getBoard(), game.whiteUsername().equals(username));
            }
            else if(game.blackUsername() != null){
                printBoard(game.game().getBoard(), !game.blackUsername().equals(username));
            }
        }
        else{
            int gameID = Integer.parseInt(line.split(" ")[1]);
            if(gameID < 1){
                throw new Exception("ID must be greater than 0");
            }
            ArrayList<Integer> ids = new ArrayList<>();
            for(GameData game : userGames){
                ids.add(game.gameID());
                if(game.gameID() == gameID){
                    if(game.whiteUsername() != null){
                        printBoard(game.game().getBoard(), game.whiteUsername().equals(username));
                    }
                    else if(game.blackUsername() != null){
                        printBoard(game.game().getBoard(), !game.blackUsername().equals(username));
                    }
                    return;
                }
            }
            String error = String.format("You are not in the game with ID of %d. Below are the games you are currently in\nGameID: ", gameID);
            for(Integer id : ids){
                error += id + " ";
            }
            throw new Exception(error);
        }

        
    }

    public void observeGame(String line) throws Exception{
        checkLogin();
        checkLength(line, 2);
        var values = line.split(" ");
        HttpURLConnection http = sendRequest(url + "/game", "GET", "", authToken);
        var result = new Gson().fromJson(receiveResponse(http).toString(), Map.class);
        GameData game = findGame(result, values[1]);
        printBoard(game.game().getBoard(), true);
    }

    private HttpURLConnection sendRequest(String url, String method, String body) throws URISyntaxException, IOException {
        URI uri = new URI(url);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);
        writeRequestBody(body, http);
        http.connect();
        return http;
    }

    private HttpURLConnection sendRequest(String url, String method, String body, String authToken) throws URISyntaxException, IOException {
        URI uri = new URI(url);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setDoOutput(true);
        http.addRequestProperty("Authorization", authToken);
        http.setRequestMethod(method);
        writeRequestBody(body, http);
        http.connect();
        return http;
    }

    private void writeRequestBody(String body, HttpURLConnection http) throws IOException {
        if (!body.isEmpty()) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
    }

    private Object receiveResponse(HttpURLConnection http) throws IOException {
        int statusCode = http.getResponseCode();

        if(statusCode != 200){
            throw new IOException(Integer.toString(statusCode));
        }        

        Object responseBody = readResponseBody(http);
        return responseBody;
    }

    private Object readResponseBody(HttpURLConnection http) throws IOException {
        Object responseBody;
        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);
            responseBody = new Gson().fromJson(inputStreamReader, Map.class);
        }
        return responseBody;
    }


    private void printBoard(ChessBoard board, boolean white){
        
        ChessPiece[][] pieces = board.getBoard();
        boolean whiteBackground = true;
        if(white){
            printLetters(false);
            for(int i = 0; i < 8; i++){
                System.out.print(8-i + " ");
                for(int j = 0; j < 8; j++){
                    printPiece(pieces[i][j], whiteBackground);
                    whiteBackground = !whiteBackground;
                }
                System.out.printf(" %d%n", 8-i);
                whiteBackground = !whiteBackground;
            }
            printLetters(false);
        }
        else{
            printLetters(true);
            for(int i = 7; i >= 0; i--){
                System.out.print(8-i + " ");
                for(int j = 7; j >= 0; j--){
                    printPiece(pieces[i][j], whiteBackground);
                    whiteBackground = !whiteBackground;
                }
                System.out.printf(" %d%n", 8-i);
                whiteBackground = !whiteBackground;
            }
            printLetters(true);
        }

    }

    private void printLetters(boolean reverse){
        String[] pos = {"a", "b", "c", "d", "e", "f", "g", "h"};
        System.out.print(" ");
        if(!reverse){
            for(String letter : pos){
                System.out.printf("  %s", letter);
            }
        }
        else{
            for(int i = 7; i >= 0; i--){
                System.out.printf("  %s", pos[i]);
            }
        }
        System.out.println("");
    }

    private void printPiece(ChessPiece piece, boolean whiteBackground){
        Predicate<ChessPiece> validPiece = x -> x != null;
        if(whiteBackground){
            System.out.printf("%s%s%s", EscapeSequences.SET_BG_COLOR_WHITE,
                validPiece.test(piece) ? pieceChar(piece) : EscapeSequences.EMPTY, 
                EscapeSequences.FULL_COLOR_RESET);
        }
        else{
            System.out.printf("%s%s%s", EscapeSequences.SET_BG_COLOR_BLACK,
                validPiece.test(piece) ? pieceChar(piece) : EscapeSequences.EMPTY, 
                EscapeSequences.FULL_COLOR_RESET);
        }
    }

    private String pieceChar(ChessPiece piece){
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            return switch (piece.getPieceType()){
                case PAWN -> EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.WHITE_PAWN;
                case KNIGHT -> EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.WHITE_KNIGHT;
                case BISHOP -> EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.WHITE_BISHOP;
                case ROOK -> EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.WHITE_ROOK;
                case QUEEN -> EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.WHITE_QUEEN;
                case KING -> EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.WHITE_KING;
            };
        }
        return switch (piece.getPieceType()){
            case PAWN -> EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.BLACK_PAWN;
            case KNIGHT -> EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.BLACK_KNIGHT;
            case BISHOP -> EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.BLACK_BISHOP;
            case ROOK -> EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.BLACK_ROOK;
            case QUEEN -> EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.BLACK_QUEEN;
            case KING -> EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.BLACK_KING;
        };
    }

    /**
     * Returns an ArrayList from the JSON http response
     * @param httpResponse JSON response from the server
     * @return ArrayList of type GameData
     */
    private ArrayList<GameData> gamesAsList(Object httpResponse){
        var result = new Gson().fromJson(httpResponse.toString(), Map.class);
        var res = new Gson().fromJson(result.get("games").toString(), ArrayList.class);
        ArrayList<GameData> currentGames = new ArrayList<>();
        for(int i = 0; i < res.size(); i++){
            GameData game = new Gson().fromJson(res.get(i).toString(), GameData.class);
            currentGames.add(game);
        }
        return currentGames;
    }

    private ArrayList<GameData> userGamesAsList() throws Exception{
        HttpURLConnection http = sendRequest(url + "/game", "GET", "", authToken);
        var result = new Gson().fromJson(receiveResponse(http).toString(), Map.class);
        var res = new Gson().fromJson(result.get("games").toString(), ArrayList.class);
        ArrayList<GameData> currentGames = new ArrayList<>();
        for(int i = 0; i < res.size(); i++){
            GameData game = new Gson().fromJson(res.get(i).toString(), GameData.class);
            if((game.whiteUsername() != null && game.whiteUsername().equals(username)) 
            || (game.blackUsername() != null && game.blackUsername().equals(username))){
                currentGames.add(game);
            }
        }
        return currentGames;
    }


    /**
     * finds the game associated with a certain game ID
     * @param httpResponse JSON response from the server
     * @param gameID String input from the user
     * @return Game if found, null if doesn't exist
     */
    private GameData findGame(Object httpResponse, String gameID) throws Exception{
        var result = new Gson().fromJson(httpResponse.toString(), Map.class);
        var res = new Gson().fromJson(result.get("games").toString(), ArrayList.class);
        int id = Integer.parseInt(gameID);
        if(id < 1){
            throw new Exception("ID must be greater than 0");
        }
        for(int i = 0; i < res.size(); i++){
            GameData game = new Gson().fromJson(res.get(i).toString(), GameData.class);
            if(game.gameID() == id){
                return game;
            }
        }
        return null;
    }

    // This is used for testing purposes
    public void resetDatabase(){
        try{
            HttpURLConnection http = sendRequest(url + "/db", "DELETE", "");
            receiveResponse(http);
        }
        catch(IOException | URISyntaxException ex){
            System.out.println("Something happened :(");
        }

    }

}