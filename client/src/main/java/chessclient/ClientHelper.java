package chessclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import ui.EscapeSequences;

public class ClientHelper {
    public static ArrayList<GameData> gamesAsList(Object httpResponse){
        var result = new Gson().fromJson(httpResponse.toString(), Map.class);
        var res = new Gson().fromJson(result.get("games").toString(), ArrayList.class);
        ArrayList<GameData> currentGames = new ArrayList<>();
        for(int i = 0; i < res.size(); i++){
            GameData game = new Gson().fromJson(res.get(i).toString(), GameData.class);
            currentGames.add(game);
        }
        return currentGames;
    }

    public static ChessPiece.PieceType toPromotion(String promotion){
        return switch(promotion.toLowerCase()){
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "queen" -> ChessPiece.PieceType.QUEEN;
            default -> null;
        };
    }

    public static int arrayToRow(int i){
        return 8-i;
    }

    public static int arrayToCol(int j){
        return j+1;
    }

    public static ChessPosition locationToPosition(String location) throws Exception{
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

    public static void checkMove(String location) throws Exception{
        Pattern pattern = Pattern.compile("[a-h][1-9]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(location);
        if(!matcher.find()){
            throw new Exception("'" + location + "' does not appear on the chess board. The format is [a-h][1-9] (e.g. a5, g1)");
        }
    }

    public static void printBoard(ChessBoard board, boolean white, Status status){
        if(status == Status.OBSERVING){
            white = false;
        }
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

    public static void printBoard(ChessBoard board, boolean white, ChessPosition init, ArrayList<ChessPosition> moves){
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

    private static void printLetters(boolean reverse){
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

    private static void printPiece(ChessPiece piece, int i, int j, boolean whiteBackground, ChessPosition init, ArrayList<ChessPosition> moves){
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

    private static void printPiece(ChessPiece piece, boolean whiteBackground){
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

    private static String pieceChar(ChessPiece piece){
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

    public static void help(Status status){
        switch(status){
            case LOGGED_IN -> {
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
            case IN_GAME -> {
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
            case OBSERVING -> {
                System.out.printf("\t%sredraw %s- redraws current chess board%s%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                    EscapeSequences.FULL_COLOR_RESET);
                System.out.printf("\t%sleave %s- leave the current chess game%s%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, EscapeSequences.SET_TEXT_COLOR_MAGENTA,
                    EscapeSequences.FULL_COLOR_RESET);
            }
            default -> {
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
    }

    public static void exceptionHandler(IOException ex){
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
}
