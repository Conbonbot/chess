import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;

public class Console {

    private static String line;
    private static Scanner scanner;
    private static String status = EscapeSequences.SET_TEXT_COLOR_RED + "[LOGGED_OUT]" + EscapeSequences.FULL_COLOR_RESET;
    private static boolean console = true;
    private static boolean loggedIn = false;
    // Change to be an instance
    private static String authToken = "";

    private static void init(){
        System.out.printf("%s%s Welcome to 240 Chess. Type Help to get started %s%n", 
                        EscapeSequences.FULL_COLOR_RESET,
                        EscapeSequences.BLACK_KING, 
                        EscapeSequences.WHITE_KING);
        
        line = "";

        scanner = new Scanner(System.in);
    }

    public static void run(){
        init();
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
                        case "login" -> login();
                        case "register" -> register();
                        // Postlogin commands
                        case "logout" -> logout();
                        case "create" -> createGame();
                        case "list" -> listGames();
                        case "observe" -> observeGame();
                        case "join" -> joinGame();
                        default -> System.out.printf("%s'%s' is not recognized as a command. Type help for a list%s%n",
                                    EscapeSequences.SET_TEXT_COLOR_RED, 
                                    line,
                                    EscapeSequences.RESET_TEXT_COLOR);
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

    private static void exceptionHandler(IOException ex){
        switch (Integer.parseInt(ex.getMessage())) {
            case 400 -> System.out.printf("%sError: bad request%s%n", EscapeSequences.SET_TEXT_COLOR_RED, EscapeSequences.FULL_COLOR_RESET);
            case 401 -> System.out.printf("%sYou are unauthorized.%s%n", EscapeSequences.SET_TEXT_COLOR_RED, EscapeSequences.FULL_COLOR_RESET);
            case 403 -> System.out.printf("%sThis has already been taken.%s%n", EscapeSequences.SET_TEXT_COLOR_RED, EscapeSequences.FULL_COLOR_RESET);
            default -> System.out.printf("%sAn error has occured. Try again.%s%n", EscapeSequences.SET_TEXT_COLOR_RED, EscapeSequences.FULL_COLOR_RESET);
        }
    }

    private static void exceptionHandler(Exception ex){

    }



    // Prelogin

    private static void help(){
        if(loggedIn){
            System.out.printf("\t%screate <NAME> %s- create a game%s%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                    EscapeSequences.FULL_COLOR_RESET);
            System.out.printf("\t%slist <NAME> %s- list current games%s%n",
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

    private static void quit(){
        System.out.println("Sounds good, have a great day");
        scanner.close();
        console = false;
        
    }

    private static void login() throws Exception{
        if(line.split(" ").length == 3){
            var values = line.split(" ");
            var body = Map.of("username", values[1], "password", values[2]);
            HttpURLConnection http = sendRequest("http://localhost:3000/session", "POST", new Gson().toJson(body));
            String response = receiveResponse(http).toString();
            authToken = response.substring(response.indexOf("authToken")+10, response.length()-1);
            loggedIn = true;
            status = EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED_IN]" + EscapeSequences.FULL_COLOR_RESET;
        }
        else{
            System.out.printf("Something is missing from your request.%nThe format is <USERNAME> <PASSWORD>%n");
        }
    }

    private static void register() throws Exception{
        if(line.split(" ").length == 4){
            // make HTTP protocol
            var values = line.split(" ");
            var body = Map.of("username", values[1], "password", values[2], "email", values[3]);
            HttpURLConnection http = sendRequest("http://localhost:3000/user", "POST", new Gson().toJson(body));
            receiveResponse(http);

            loggedIn = true;
            status = EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED_IN]" + EscapeSequences.FULL_COLOR_RESET;
        }
        else{
            System.out.printf("Something is missing%nThe format is <USERNAME> <PASSWORD> <EMAIL>%n");
        }
    }

    // Postlogin

    private static void logout() throws Exception{
        if(loggedIn){
            // make request

            loggedIn = false;
            status = "[LOGGED_OUT]";
        }
        else{
            System.out.printf("You have to log in first%n");
        }
    }

    private static void createGame(){
        if(loggedIn){

        }
        else{
            System.out.printf("Hold on partner%n");
        }

    }

    private static void listGames(){
        if(loggedIn){

        }
        else{
            System.out.printf("Hold on partner%n");
        }
    }

    private static void joinGame(){
        if(loggedIn){

        }
        else{
            System.out.printf("Hold on partner%n");
        }
    }

    private static void observeGame(){
        if(loggedIn){

        }
        else{
            System.out.printf("Hold on partner%n");
        }
    }

    private static HttpURLConnection sendRequest(String url, String method, String body) throws URISyntaxException, IOException {
        URI uri = new URI(url);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);
        writeRequestBody(body, http);
        http.connect();
        System.out.printf("= Request =========\n[%s] %s\n\n%s\n\n", method, url, body);
        return http;
    }

    private static void writeRequestBody(String body, HttpURLConnection http) throws IOException {
        if (!body.isEmpty()) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
    }

    private static Object receiveResponse(HttpURLConnection http) throws IOException {
        int statusCode = http.getResponseCode();
        String statusMessage = http.getResponseMessage();

        if(statusCode != 200){
            throw new IOException(Integer.toString(statusCode));
        }        

        Object responseBody = readResponseBody(http);
        System.out.printf("= Response =========\n[%d] %s\n\n%s\n\n", statusCode, statusMessage, responseBody);
        return responseBody;
    }

    private static Object readResponseBody(HttpURLConnection http) throws IOException {
        Object responseBody;
        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);
            responseBody = new Gson().fromJson(inputStreamReader, Map.class);
        }
        return responseBody;
    }
}
