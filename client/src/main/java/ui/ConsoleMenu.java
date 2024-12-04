package ui;

import chess.ChessBoard;
import chess.ChessGame;
import model.SimpleGameData;
import model.UserData;
import model.requestresult.*;
import network.ServerFacade;

import java.util.Collection;
import java.util.Scanner;

public class ConsoleMenu {
    private boolean loggedIn;
    private ServerFacade serverFacade;
    String authToken;
    Scanner scanner;
    Boolean quit = false;
    private boolean inGame;
    private ChessGame.TeamColor inGameColor;


    public ConsoleMenu() {
        System.out.println("Welcome to 240 Chess!!");
        loggedIn = false;
        serverFacade = new ServerFacade(8080);
        scanner = new Scanner(System.in);
        authToken = null;
        inGame = false;
        inGameColor = ChessGame.TeamColor.WHITE;

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
                            printBoard(inGameColor);
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

    private void printBoard(ChessGame.TeamColor color) {
        BoardUI.main(new ChessGame().getBoard(), color);
    }

    private void leave() {

    }

    private void makeMove(){

    }

    private void resign(){

    }

    private void highlightLegalMoves(){

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
                printBoard(color);
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
            BoardUI.main(new ChessGame().getBoard(), ChessGame.TeamColor.WHITE);
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
