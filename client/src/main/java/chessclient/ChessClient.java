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
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
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
    private String username = "";
    private boolean isWhite;
    private boolean isObserving = false;
    private String userGameID = "";
    private String line;
    // websocket
    private WebSocketFacade ws;
    private Status status = Status.LOGGED_OUT;

    private void init(){
        System.out.printf("%s%s Welcome to 240 Chess. Type Help to get started %s%n", 
                        EscapeSequences.FULL_COLOR_RESET,
                        EscapeSequences.BLACK_KING, 
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
        printBoard(message.getBoard(), message.isWhite());
        printStatus();
    }

    private void highlightPing(HighlightMessage message){
        printBoard(message.getBoard(), isWhite, message.getPos(), message.getLocations());
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
        printStatus();
    }

    private void notification(NotificationMessage message) {
        System.out.printf("%n--------%n%sNotification: %s%s%n--------%n",
            EscapeSequences.SET_TEXT_COLOR_BLUE,
            message.getNotification(),
            EscapeSequences.FULL_COLOR_RESET);
        printStatus();
        
    }

    // Priting status

    private void printStatus(){
        String str = switch(status){
            case LOGGED_IN -> EscapeSequences.SET_TEXT_COLOR_BLUE + "[LOGGED_IN]" + EscapeSequences.FULL_COLOR_RESET;
            case LOGGED_OUT -> EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "[LOGGED_OUT]" + EscapeSequences.FULL_COLOR_RESET;
            case IN_GAME -> EscapeSequences.SET_TEXT_COLOR_GREEN + "[IN_GAME]" + EscapeSequences.FULL_COLOR_RESET;
            case OBSERVING -> EscapeSequences.SET_TEXT_COLOR_GREEN + "[OBSERVING]" + EscapeSequences.FULL_COLOR_RESET;
            default -> "";
        };
        System.out.printf("%s >>> ", str);
    }

    // Running 



    public void run(){
        init();
        line = "";
        while (!line.toLowerCase().equals("quit")){
            if(console){
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
            // Prelogin commands
            case "login" -> login(line);
            case "register" -> register(line); 
            default -> {
                System.out.printf("%s'%s' is not recognized as a command. Type help for a list%s%n",
                        EscapeSequences.SET_TEXT_COLOR_RED, 
                        line,
                        EscapeSequences.RESET_TEXT_COLOR);
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
            case "logout" -> logout();
            case "create" -> createGame(line);
            case "list" -> listGames();
            case "observe" -> observeGame(line);
            case "join" -> joinGame(line);
            default -> {
                System.out.printf("%s'%s' is not recognized as a command. Type help for a list%s%n",
                        EscapeSequences.SET_TEXT_COLOR_RED, 
                        line,
                        EscapeSequences.RESET_TEXT_COLOR);
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
            case "quit" -> {
                quit();
            }
            case "help" -> {
                help();
                printStatus();
            }
            case "resign" -> {
                resign();
            }
            case "move" -> {
                makeMove(line);
            }
            case "highlight" -> {
                highlightMoves(line);
            }
            case "leave" -> {  
                leaveGame(line);
            }
            case "redraw" -> { 
                redrawBoard(line);
            }
            default -> { 
                System.out.printf("%s'%s' is not recognized as a command. Type help for a list%s%n",
                        EscapeSequences.SET_TEXT_COLOR_RED, 
                        line,
                        EscapeSequences.RESET_TEXT_COLOR);
                printStatus();
            }
        }
    }

    // Exceptions and checkers

    private void exceptionHandler(IOException ex){
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

    private void checkGame() throws Exception{
        if(isObserving || !userGameID.isEmpty()){
            return;
        }
        throw new Exception("You are not currently in any games");
    }

    private void checkMove(String location) throws Exception{
        Pattern pattern = Pattern.compile("[a-h][1-9]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(location);
        if(!matcher.find()){
            throw new Exception("'" + location + "' does not appear on the chess board. The format is [a-h][1-9] (e.g. a5, g1)");
        }
    }

    // Prelogin
    private void help(){
        if(!username.isEmpty() && userGameID.isEmpty()){
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
        else if(!username.isEmpty() && !userGameID.isEmpty()){
            System.out.printf("\t%sredraw %s- redraws current chess board%s%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                    EscapeSequences.FULL_COLOR_RESET);
            System.out.printf("\t%sleave %s- leave the current chess game%s%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                    EscapeSequences.FULL_COLOR_RESET);
            System.out.printf("\t%smove <START> <END> <PROMOTION?>%s- moves a chess piece from start to end location." +
                    "For pawns, include the promotion rank (queen, knight, rook, bishop) %s%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                    EscapeSequences.FULL_COLOR_RESET);
            System.out.printf("\t%sresign %s- Forfeit the game%s%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                    EscapeSequences.FULL_COLOR_RESET);
            System.out.printf("\t%shighlight <LOCATION> %s- highlights the locations a piece can move to%s%n",
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
        username = values[1];
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
        username = values[1];
        status = Status.LOGGED_IN;
        System.out.printf("Welcome to chess! use the command 'help' to show commands!%n");
        printStatus();
    }

    // Postlogin
    public void logout() throws Exception{
        checkLogin();
        checkLength(line, 1);
        HttpURLConnection http = sendRequest(url + "/session", "DELETE", "", authToken);
        receiveResponse(http);
        username = "";
        authToken = "";
        status = Status.LOGGED_OUT;
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

    public void listGames() throws Exception{
        checkLogin();
        checkLength(line, 1);
        HttpURLConnection http = sendRequest(url + "/game", "GET", "", authToken);
        var games = receiveResponse(http);
        ArrayList<GameData> gamesList = gamesAsList(games);
        if(gamesList.isEmpty()){
            System.out.printf("There are currently no active games%nUse the command '%screate <NAME>%s' to start one!%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE,
                    EscapeSequences.FULL_COLOR_RESET);
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
        checkLogin();
        checkLength(line,3);
        var values = line.split(" ");
        ws = new WebSocketFacade(url, this);
        ws.connect(authToken, values[1], values[2]);
        // wait for response
        status = Status.IN_GAME;
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
    }

    public void resign() throws Exception{
        checkLogin();
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
        checkLength(line, 3, 4);
        var values = line.split(" ");
        ws = new WebSocketFacade(url, this);
        ChessMove move;
        if(values.length == 3){
            move = new ChessMove(locationToPosition(values[1]), locationToPosition(values[2]));
        }
        else{
            move = new ChessMove(locationToPosition(values[1]), locationToPosition(values[2]), toPromotion(values[3]));
        }
        ws.makeMove(authToken, userGameID, isWhite, move);
    }

    public void highlightMoves(String line) throws Exception{
        checkLogin();
        checkLength(line, 2);
        // websocket
        ChessPosition pos = locationToPosition(line.split(" ")[1]);
        ws = new WebSocketFacade(url, this);
        ws.highlight(authToken, userGameID, pos, isWhite);
    }

    public void redrawBoard(String line) throws Exception{
        checkLogin();
        checkLength(line, 1);
        checkGame();

        ws = new WebSocketFacade(url, this);
        ws.loadBoard(authToken, userGameID, isWhite);
        
    }

    public void observeGame(String line) throws Exception{
        checkLogin();
        checkLength(line, 2);
        // websocket
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

    private void printBoard(ChessBoard board, boolean white, ChessPosition init, ArrayList<ChessPosition> moves){
        ChessPiece[][] pieces = board.getBoard();
        boolean whiteBackground = true;
        if(white){
            printLetters(false);
            for(int i = 0; i < 8; i++){
                System.out.print(8-i + " ");
                for(int j = 0; j < 8; j++){
                    // Change background color
                    printPiece(pieces[i][j], i, j, whiteBackground, init, moves);
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
                    printPiece(pieces[i][j], i, j, whiteBackground, init, moves);
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

    private void printPiece(ChessPiece piece, int i, int j, boolean whiteBackground, ChessPosition init, ArrayList<ChessPosition> moves){
        Predicate<ChessPiece> validPiece = x -> x != null;
        if(whiteBackground){
            if(init.equals(new ChessPosition(arrayToRow(i), arrayToCol(j)))){
                System.out.printf("%s%s%s", EscapeSequences.SET_BG_COLOR_GREEN,
                    validPiece.test(piece) ? pieceChar(piece) : EscapeSequences.EMPTY, 
                    EscapeSequences.FULL_COLOR_RESET);
            }
            else if(moves.contains(new ChessPosition(arrayToRow(i), arrayToCol(j)))){
                System.out.printf("%s%s%s", EscapeSequences.SET_BG_COLOR_YELLOW,
                    validPiece.test(piece) ? pieceChar(piece) : EscapeSequences.EMPTY, 
                    EscapeSequences.FULL_COLOR_RESET);
            }
            else{
                System.out.printf("%s%s%s", EscapeSequences.SET_BG_COLOR_WHITE,
                    validPiece.test(piece) ? pieceChar(piece) : EscapeSequences.EMPTY, 
                    EscapeSequences.FULL_COLOR_RESET);
            }
        }
        else{
            if(init.equals(new ChessPosition(arrayToRow(i), arrayToCol(j)))){
                System.out.printf("%s%s%s", EscapeSequences.SET_BG_COLOR_GREEN,
                    validPiece.test(piece) ? pieceChar(piece) : EscapeSequences.EMPTY, 
                    EscapeSequences.FULL_COLOR_RESET);
            }
            else if(moves.contains(new ChessPosition(arrayToRow(i), arrayToCol(j)))){
                System.out.printf("%s%s%s", EscapeSequences.SET_BG_COLOR_YELLOW,
                    validPiece.test(piece) ? pieceChar(piece) : EscapeSequences.EMPTY, 
                    EscapeSequences.FULL_COLOR_RESET);
            }
            else{
                System.out.printf("%s%s%s", EscapeSequences.SET_BG_COLOR_BLACK,
                    validPiece.test(piece) ? pieceChar(piece) : EscapeSequences.EMPTY, 
                    EscapeSequences.FULL_COLOR_RESET);
            }
        }
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

    private ChessPiece.PieceType toPromotion(String promotion){
        return switch(promotion.toLowerCase()){
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "queen" -> ChessPiece.PieceType.QUEEN;
            default -> null;
        };
    }

    private ChessPosition locationToPosition(String location) throws Exception{
        checkMove(location);
        // a 2 -> row 2, col 1
        // col, row
        char let = location.charAt(0);
        char row = location.charAt(1);
        int col = switch(let){
            case 'a' -> 1;
            case 'b' -> 2;
            case 'c' -> 3;
            case 'd' -> 4;
            case 'e' -> 5;
            case 'f' -> 6;
            case 'g' -> 7;
            case 'h' -> 8;
            default -> throw new Exception("Invalid input");
        };
        return new ChessPosition(row-'0', col);
    }

    private int arrayToRow(int i){
        return 8-i;
    }

    private int arrayToCol(int j){
        return j+1;
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