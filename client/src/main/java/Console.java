import java.util.Scanner;

public class Console {

    private static String line;
    private static Scanner scanner;
    private static String status = "[LOGGED_OUT]";
    private static boolean console = true;
    private static boolean loggedIn = false;

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
                switch(line) {
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
            else{
                System.out.printf("%nGoodbye%n");
                scanner.close();
                break;
            }
        }
    }

    // Prelogin

    private static void help(){
        if(loggedIn){
            System.out.println("Here are the commands");
        }
        else{
            System.out.println("I'll still give you the commands, but you should login. It's cool");
        }
        
    }

    private static void quit(){
        System.out.println("Sounds good, have a great day");
        scanner.close();
        console = false;
        
    }

    private static void login(){
        System.out.println("So you want to login?");
    }

    private static void register(){

    }

    // Postlogin

    private static void logout(){
        if(loggedIn){

        }
        else{
            System.out.printf("Hold on partner%n");
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
}
