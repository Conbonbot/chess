import java.util.Scanner;

public class Console {
    public static void run(){
        System.out.printf("%s Welcome to 240 Chess. Type Help to get started %s%n", 
                        EscapeSequences.BLACK_KING, 
                        EscapeSequences.WHITE_KING);
        String line = "";

        Scanner scanner = new Scanner(System.in);

        while (!line.toLowerCase().equals("quit")) {
            System.out.printf("Say something%n>>> ");
            if(scanner.hasNextLine()) {
                System.out.println(scanner.hasNextLine());
                line = scanner.nextLine();

                System.out.printf("You said: %s%n", line);
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

    }

    private static void quit(){

    }

    private static void login(){

    }

    private static void register(){

    }
}
