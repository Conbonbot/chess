package chessclient;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;

import chess.ChessMove;
import chess.ChessPosition;
import model.GameData;
import ui.EscapeSequences;
import websocket.ServerMessageObserver;
import websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.HighlightMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class ChessClient implements ServerMessageObserver{
    private Scanner scanner;
    private boolean console = true;
    private final String url;
    private String authToken = "";
    private boolean isWhite;
    private String userGameID = "";
    private String line;
    private WebSocketFacade ws;
    private Status status = Status.LOGGED_OUT;
    private boolean connecting = false;
    private boolean observing = false;
    private boolean makeMove = false;

    public void init(){
        System.out.printf("%s%s Welcome to 240 Chess. Type Help to get started %s%n", EscapeSequences.FULL_COLOR_RESET, EscapeSequences.BLACK_KING, 
                        EscapeSequences.WHITE_KING);
        printStatus();
        scanner = new Scanner(System.in);
    }

    public ChessClient(int port){
        url = "http://localhost:" + port;
        scanner = new Scanner(System.in);
    }
    
    @Override
    public void message(ServerMessage message, String strMessage){
        switch(message.getServerMessageType()){
            case LOAD_GAME -> loadGame(new Gson().fromJson(strMessage, LoadGameMessage.class));
            case ERROR -> error(new Gson().fromJson(strMessage, ErrorMessage.class));
            case NOTIFICATION -> notification(new Gson().fromJson(strMessage, NotificationMessage.class));
            case RESIGN -> resignPing();
            case LEAVE -> leavePing();
            case HIGHLIGHT -> highlightPing(new Gson().fromJson(strMessage, HighlightMessage.class));
            default -> throw new IllegalArgumentException("Unexpected value: " + message.getServerMessageType());
        };
    }

    private void resignPing(){
        System.out.printf("You have resigned and the other player has won.%n");
        status = Status.LOGGED_IN;
    }

    private void leavePing(){
        status = Status.LOGGED_IN;
    }

    private void loadGame(LoadGameMessage message) {
        if(connecting){
            connecting = false;
            status = Status.IN_GAME;
        }
        else if(observing){
            observing = false;
            isWhite = true;
            status = Status.OBSERVING;
        }
        else if(!makeMove){
            System.out.println("");
        }
        ClientHelper.printBoard(message.getBoard(), isWhite, status);
        printStatus();
        makeMove = false;
    }

    private void highlightPing(HighlightMessage message){
        ClientHelper.printBoard(message.getBoard(), isWhite, message.getPos(), message.getLocations());
        printStatus();
    }

    private void error(ErrorMessage message) {
        System.out.printf("%s%s%s%n", EscapeSequences.SET_TEXT_COLOR_RED,
            message.getErrorMessage(),
            EscapeSequences.FULL_COLOR_RESET);
        if(message.getErrorMessage().contains("invalid game")){
            userGameID = "";
            status = Status.LOGGED_IN;
        }
        if(connecting){
            connecting = false;
            status = Status.LOGGED_IN;
            userGameID = "";
        }
        printStatus();
    }

    private void notification(NotificationMessage message) {
        System.out.printf("%n--------%n%sNotification: %s%s%n--------%n",
            EscapeSequences.SET_TEXT_COLOR_BLUE,
            message.getNotification(),
            EscapeSequences.FULL_COLOR_RESET);
        if(message.getNotification().contains("resigned")){
            status = Status.LOGGED_IN;
            userGameID = "";
        }
        printStatus();
    }

    private void printStatus(){
        String str = switch(status){
            case LOGGED_IN -> EscapeSequences.SET_TEXT_COLOR_BLUE + "[LOGGED_IN]" + EscapeSequences.FULL_COLOR_RESET;
            case LOGGED_OUT -> EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "[LOGGED_OUT]" + EscapeSequences.FULL_COLOR_RESET;
            case IN_GAME -> EscapeSequences.SET_TEXT_COLOR_GREEN + "[IN_GAME]" + EscapeSequences.FULL_COLOR_RESET;
            case OBSERVING -> EscapeSequences.SET_TEXT_COLOR_GREEN + "[OBSERVING]" + EscapeSequences.FULL_COLOR_RESET;
            default -> "";
        };
        System.out.printf("%s >>> ", str);
        line = "";
    }

    public void run() throws InterruptedException{
        init();
        line = "";
        while (!line.toLowerCase().equals("quit")){
            if(console){
                Thread.sleep(200);
                try{
                    switch(status){
                        case LOGGED_OUT -> runPreLogin();
                        case LOGGED_IN -> runPostLogin();
                        case IN_GAME -> runGameplay();
                        case OBSERVING -> runGameplay();
                    }
                }
                catch(IOException ex){
                    exceptionHandler(ex);
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

    private void runPreLogin() throws Exception{
        if(scanner.hasNextLine()){
            line = scanner.nextLine();
        }
        else{
            console = false;
            return;
        }
        switch(line.split(" ")[0]){
            case "quit" -> quit();
            case "help" -> help();
            case "login" -> login(line);
            case "register" -> register(line); 
            default -> {
                System.out.printf("%s'%s' is not recognized as a command. Type help for a list%s%n", EscapeSequences.SET_TEXT_COLOR_RED, 
                        line, EscapeSequences.RESET_TEXT_COLOR);
                printStatus();
            }

        }
    }

    private void runPostLogin() throws Exception{
        if(scanner.hasNextLine()){
            line = scanner.nextLine();
        }
        else{
            console = false;
            return;
        }
        switch(line.split(" ")[0]){
            case "quit" -> quit();
            case "help" -> help();
            case "logout" -> logout(line);
            case "create" -> createGame(line);
            case "list" -> listGames(line);
            case "observe" -> observeGame(line);
            case "join" -> joinGame(line);
            default -> {
                System.out.printf("%s'%s' is not recognized as a command. Type help for a list%s%n", EscapeSequences.SET_TEXT_COLOR_RED, 
                        line, EscapeSequences.RESET_TEXT_COLOR);
                printStatus();
            }
        }
    }

    private void runGameplay() throws Exception{
        if(scanner.hasNextLine()){
            line = scanner.nextLine();
        }
        else{
            console = false;
            return;
        }
        switch(line.split(" ")[0]){
            case "quit" -> quit();
            case "help" -> help();
            case "resign" -> resign(line);
            case "move" -> makeMove(line);
            case "highlight" -> highlightMoves(line);
            case "leave" -> leaveGame(line);
            case "redraw" -> redrawBoard(line);
            default -> { 
                System.out.printf("%s'%s' is not recognized as a command. Type help for a list%s%n", EscapeSequences.SET_TEXT_COLOR_RED, 
                    line, EscapeSequences.RESET_TEXT_COLOR);
                printStatus();
            }
        }
    }

    private void exceptionHandler(IOException ex){
        ClientHelper.exceptionHandler(ex);
        printStatus();
    }

    private void exceptionHandler(Exception ex){
        System.out.printf("%s%s%s%n", EscapeSequences.SET_TEXT_COLOR_RED, ex.getMessage(), EscapeSequences.FULL_COLOR_RESET);
        printStatus();
    }

    private void checkLogin() throws Exception{
        if(authToken.isEmpty()){
            throw new Exception("You must log in to use that command.\n");
        }
    }

    private void checkGame() throws Exception{
        if(status != Status.IN_GAME){
            throw new Exception("You cannot use that command as an oberver.");
        }
    }

    private void checkLength(String line, int amount) throws Exception{
        if(line.split(" ").length != amount){
            throw new Exception("The format is incorrect, use the command 'help' to show correct format");
        }
    }


    private void checkLength(String line, int lower, int upper) throws Exception{
        if(line.split(" ").length < lower || line.split(" ").length > upper){
            throw new Exception("The format is incorrect, use the command 'help' to show correct format");
        }
    }


    private void help(){
        ClientHelper.help(status);
        printStatus();
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
        status = Status.LOGGED_IN;
        System.out.printf("Welcome back %s!%n", values[1]);
        printStatus();
    }

    public void register(String line) throws Exception{
        checkLength(line, 4);
        var values = line.split(" ");
        var body = Map.of("username", values[1], "password", values[2], "email", values[3]);
        HttpURLConnection http = sendRequest(url + "/user", "POST", new Gson().toJson(body));
        String response = receiveResponse(http).toString();
        authToken = response.substring(response.indexOf("authToken")+10, response.length()-1);
        status = Status.LOGGED_IN;
        System.out.printf("Welcome to chess! use the command 'help' to show commands!%n");
        printStatus();
    }

    public void logout(String line) throws Exception{
        checkLogin();
        checkLength(line, 1);
        HttpURLConnection http = sendRequest(url + "/session", "DELETE", "", authToken);
        receiveResponse(http);
        authToken = "";
        status = Status.LOGGED_OUT;
        printStatus();
    }

    public void createGame(String line) throws Exception{
        checkLogin();
        checkLength(line, 2);
        String gameName = line.split(" ")[1];
        var body = Map.of("gameName", gameName);
        HttpURLConnection http = sendRequest(url + "/game", "POST", new Gson().toJson(body), authToken);
        receiveResponse(http);
        System.out.printf("The game '%s' has been created!%n", gameName);
        printStatus();
    }

    public void listGames(String line) throws Exception{
        checkLogin();
        checkLength(line, 1);
        HttpURLConnection http = sendRequest(url + "/game", "GET", "", authToken);
        var games = receiveResponse(http);
        ArrayList<GameData> gamesList = ClientHelper.gamesAsList(games);
        if(gamesList.isEmpty()){
            System.out.printf("There are currently no active games%nUse the command '%screate <NAME>%s' to start one!%n",
                EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.FULL_COLOR_RESET);
        }
        else{
            System.out.printf("Below are the current games\n");
            for(int i = 0; i < gamesList.size(); i++){
                System.out.printf("ID: %d | game: %s | white: %s | black: %s\n", gamesList.get(i).gameID(), gamesList.get(i).gameName(),
                            gamesList.get(i).whiteUsername(), gamesList.get(i).blackUsername());
            }
        }
        printStatus();
    }

    public void joinGame(String line) throws NumberFormatException, Exception{
        connecting = true;
        checkLogin();
        checkLength(line,3);
        var values = line.split(" ");
        ws = new WebSocketFacade(url, this);
        ws.connect(authToken, values[1], values[2]);
        userGameID = values[1];
        isWhite = values[2].toLowerCase().equals("white");
    }

    public void leaveGame(String line) throws Exception{
        checkLogin();
        checkLength(line, 1);
        ws = new WebSocketFacade(url, this);
        ws.leave(authToken, userGameID);
        System.out.println("Leaving game...");
        userGameID = "";
        status = Status.LOGGED_IN;
        printStatus();
    }

    public void resign(String line) throws Exception{
        checkLogin();
        checkGame();
        checkLength(line, 1);
        if(userGameID.isEmpty()){
            throw new Exception("You are not connected to any games");
        }
        System.out.println("Are you sure you want to resign? (type 'yes' to confirm, anything else as no)");
        printStatus();
        String resLine = "";
        while(resLine.isEmpty()){
            if(scanner.hasNextLine()){
                resLine = scanner.nextLine();
                if(resLine.equals("yes")){
                    ws = new WebSocketFacade(url, this);
                    ws.resign(authToken, userGameID);
                }
                else{
                    System.out.println("No resignation, you will stay in the game");
                }
                break;
            }
        }
    }

    public void makeMove(String line) throws Exception{
        checkLogin();
        checkGame();
        checkLength(line, 3, 4);
        var values = line.split(" ");
        ws = new WebSocketFacade(url, this);
        ChessMove move;
        if(values.length == 3){
            move = new ChessMove(ClientHelper.locationToPosition(values[1]), ClientHelper.locationToPosition(values[2]));
        }
        else{
            move = new ChessMove(ClientHelper.locationToPosition(values[1]), 
                ClientHelper.locationToPosition(values[2]), ClientHelper.toPromotion(values[3]));
        }
        makeMove = true;
        ws.makeMove(authToken, userGameID, isWhite, move);
    }

    public void highlightMoves(String line) throws Exception{
        checkLogin();
        checkGame();
        checkLength(line, 2);
        ChessPosition pos = ClientHelper.locationToPosition(line.split(" ")[1]);
        ws = new WebSocketFacade(url, this);
        ws.highlight(authToken, userGameID, pos, isWhite);
    }

    public void redrawBoard(String line) throws Exception{
        checkLogin();
        checkLength(line, 1);
        ws = new WebSocketFacade(url, this);
        ws.loadBoard(authToken, userGameID, isWhite);
    }

    public void observeGame(String line) throws Exception{
        observing = true;
        checkLogin();
        checkLength(line, 2);
        ws = new WebSocketFacade(url, this);
        ws.observe(authToken, line.split(" ")[1]);
        status = Status.OBSERVING;
        userGameID = line.split(" ")[1];
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