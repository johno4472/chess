package ui;

import chess.*;
import model.SimpleGameData;
import model.UserData;
import model.requestresult.*;
import network.ServerFacade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

public class ConsoleMenu {
    private boolean loggedIn;
    private ServerFacade serverFacade;
    String authToken;
    Scanner scanner;
    Boolean quit = false;
    private boolean inGame;
    private ChessGame.TeamColor inGameColor;
    private int gameID;
    private ChessGame chessGame;
    private boolean observing;


    public ConsoleMenu() {
        System.out.println("Welcome to 240 Chess!!");
        loggedIn = false;
        serverFacade = new ServerFacade(8080);
        scanner = new Scanner(System.in);
        authToken = null;
        inGame = false;
        inGameColor = ChessGame.TeamColor.WHITE;
        observing = false;

        runConsole();
    }

    private void runConsole() {
        while (!quit){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("What would you like to do? (Select the number of the option you prefer)");
            if (loggedIn){
                if (inGame){
                    System.out.println("1. Help\n2. Redraw Chessboard\n3. Leave\n4. Make Move\n5. Resign\n6. Highlight Legal Moves");

                    String choice = scanner.nextLine();
                    switch (choice){
                        case "1":
                            help();
                            break;
                        case "2":
                            redrawBoard();
                            break;
                        case "3":
                            leave();
                            break;
                        case "4":
                            makeMove();
                            break;
                        case "5":
                            resign();
                            break;
                        case "6":
                            highlightLegalMoves();
                            break;
                    }
                }
                else if (observing) {
                    System.out.println("1. Help\n2. Redraw Chessboard\n3. Leave");

                    String choice = scanner.nextLine();
                    switch (choice){
                        case "1":
                            help();
                            break;
                        case "2":
                            redrawBoard();
                            break;
                        case "3":
                            leave();
                    }
                }
                else {
                    System.out.println("1. Create Game\n2. List Games\n3. Join Game\n4. Observe Game\n5. Logout\n6. Help");

                    String choice = scanner.nextLine();
                    switch (choice){
                        case "1":
                            createGame();
                            break;
                        case "2":
                            listGames();
                            break;
                        case "3":
                            joinGame();
                            //BoardUI.buildBoard();
                            break;
                        case "4":
                            observeGame();
                            break;
                        case "5":
                            logout();
                            break;
                        case "6":
                            help();
                            break;
                    }
                }
            }
            else {
                System.out.println("1. Register\n2. Login\n3. Quit\n4. Help\n");

                String choice = scanner.nextLine();
                switch (choice) {
                    case "1":
                        register();
                        break;
                    case "2":
                        login();
                        break;
                    case "3":
                        quit = true;
                        break;
                    case "4":
                        help();
                        break;
                }
            }
        }
        System.out.println("'til we meet again!");
    }

    private void redrawBoard() {
        serverFacade.makeMove(authToken, gameID, null);
    }

    private void leave() {
        serverFacade.leaveGame(new JoinGameRequest(inGameColor, gameID, authToken));
        inGame = false;
        System.out.println("You have left the game.");
    }

    private ArrayList<Integer> convertMoveToInt(String move){
        ArrayList<Integer> intArray = new ArrayList<Integer>();
        for (int i = 0; i < move.length(); i++){
            switch (move.charAt(i)){
                case '1':
                case 'a':
                    intArray.add(1);
                    break;
                case '2':
                case 'b':
                    intArray.add(2);
                    break;
                case '3':
                case 'c':
                    intArray.add(3);
                    break;
                case '4':
                case 'd':
                    intArray.add(4);
                    break;
                case '5':
                case 'e':
                    intArray.add(5);
                    break;
                case '6':
                case 'f':
                    intArray.add(6);
                    break;
                case '7':
                case 'g':
                    intArray.add(7);
                    break;
                case '8':
                case 'h':
                    intArray.add(8);
                    break;
            }
        }
        return intArray;
    }


    private void makeMove(){
        System.out.println("Where do you want to move?");
        System.out.println("Please input your move exactly like this: StartrowStartcolumnEndrowEndColumn (example below)");
        System.out.println("1a3c");
        String move = scanner.nextLine();
        ArrayList<Integer> intList = convertMoveToInt(move);
        ChessMove chessMove = new ChessMove(new ChessPosition(intList.get(0), intList.get(1)),
                new ChessPosition(intList.get(2), intList.get(3)), null);
        serverFacade.makeMove(authToken, gameID, chessMove);

    }

    private void resign(){
        System.out.println("Are you sure you want to resign? You will automatically lose if you do.\n1. Yes\n2. No");
        String response = scanner.nextLine();
        if (Objects.equals(response, "1")){
            serverFacade.resign(new JoinGameRequest(inGameColor, gameID, authToken));
        }
    }

    private void highlightLegalMoves(){
        System.out.println("Which piece would you like to see the available moves for?");
        System.out.println("(Type the coordinates of that piece)");
        String piece = scanner.nextLine();
        ArrayList<Integer> intArray = convertMoveToInt(piece);
        serverFacade.highlightLegalMoves(authToken, gameID, intArray);
    }

    private void register() {
        System.out.println("Enter username: ");
        String username = scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.nextLine();
        System.out.println("Enter email: ");
        String email = scanner.nextLine();

        RegisterResponse response = serverFacade.register(new UserData(username, password, email));
        if (response.message() == null) {
            System.out.println("Registered. Welcome " + response.username() + "!\n");
            authToken = response.authToken();
            loggedIn = true;
        }
        else {
            System.out.println("It looks like something went wrong. That username is probably already taken.");
        }
    }

    private void login() {
        System.out.println("Enter username: ");
        String username = scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.nextLine();

        LoginResponse response = serverFacade.login(new LoginRequest(username, password));
        if (response.message() == null) {
            authToken = response.authToken();
            System.out.println("Logged in. Welcome " + username + "!\n");
            loggedIn = true;
        }
        else {
            System.out.println("Looks like either your username or password are incorrect.");
        }
    }

    private void createGame(){
        System.out.println("Enter gameName: ");
        String gameName = scanner.nextLine();

        CreateGameResponse response = serverFacade.createGame(new CreateGameRequest(gameName, authToken));
        if (response.message() == null) {
            System.out.println("Game created. Game ID is: " + response.gameID());
        }
        else {
            System.out.println("Something went wrong. It was probably an invalid ID.");
        }
    }

    private void listGames() {
        ListGamesResponse response = serverFacade.listGames(new ListGamesRequest(authToken));
        Collection<SimpleGameData> games = response.games();
        int userGameID = 0;
        if (games.isEmpty()){
            System.out.println("There are no games to list.");
        }
        for (SimpleGameData game: games){
            userGameID += 1;
            System.out.println("Game ID: " + userGameID + ",  White Username: " + game.whiteUsername() +
                    ",  Black Username: " + game.blackUsername() + ",  Game Name: " + game.gameName() + ";");
        }
        System.out.println();
    }

    private void joinGame() {
        System.out.println("Enter the ID number of the game you'd like to join.");
        int userGameID = 0;
        try {
            userGameID = Integer.parseInt(scanner.nextLine());
        } catch (Exception e){
            System.out.println("Type a number please");
            return;
        }
        int dataGameID = convertToDataGameID(userGameID);
        System.out.println("Would you like to be: white/black?");
        String colorChoice = scanner.nextLine();
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        Boolean colorRepeat = true;
        while (colorRepeat) {
            if (colorChoice.equalsIgnoreCase("white")) {
                color = ChessGame.TeamColor.WHITE;
                colorRepeat = false;
            } else if (colorChoice.equalsIgnoreCase("black")) {
                color = ChessGame.TeamColor.BLACK;
                inGameColor = ChessGame.TeamColor.BLACK;
                colorRepeat = false;
            } else {
                System.out.println("Invalid entry. Please enter one of the two options as shown.");
                colorChoice = scanner.nextLine();
            }
        }
        try {
            JoinGameResponse response = serverFacade.joinGame(new JoinGameRequest(
                    color, dataGameID, authToken));
            if (response.message() == null) {
                this.gameID = userGameID;
                inGame = true;
            } else {
                System.out.println("Something went wrong. You could have put in an invalid ID or chosen/written an incorrect color entry");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }

    private void observeGame() {
        System.out.println("Which game do you want to observe?");
        int gameID = Integer.parseInt(scanner.nextLine());
        ListGamesResponse response = serverFacade.observeGame(gameID, authToken);
        if (response.games().size() >= gameID) {
            observing = true;
        }
        else {
            System.out.println("Looks like there was a mistake. Double check your entries.");
        }
    }

    private int convertToDataGameID(int userGameID) {
        ListGamesResponse response = serverFacade.listGames(new ListGamesRequest(authToken));
        int counter = 0;
        for (SimpleGameData game : response.games()) {
            counter += 1;
            if (counter == userGameID){
                return game.gameID();
            }
        }
        return 0;
    }

    private void logout() {
        serverFacade.logout(authToken);
        System.out.println("Logged out. See ya later!\n");
        loggedIn = false;
    }

    private void help() {
        if (loggedIn) {
            if (inGame) {
                System.out.println("Just let me know what you want to do for gameplay!" +
                        " Select the number corresponding with your choice");
            } else {
                System.out.println("Just let me know what you want to do now that you're logged in!" +
                        " Select the number corresponding with your choice");
            }
        } else {
            System.out.println("Just let me know what you want to do! If you haven't created an account yet, go ahead and register." +
                    "Select the number corresponding with your choice");
        }
    }
}
