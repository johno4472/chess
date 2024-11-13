package ui;

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


    public ConsoleMenu() {
        System.out.println("Welcome to 240 Chess!!");
        loggedIn = false;
        serverFacade = new ServerFacade();
        scanner = new Scanner(System.in);
        authToken = null;

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
                System.out.println("1. Create Game\n2. List Games\n3. Join Game\n4. Observe Game\n5. Logout\n6. Quit\n7. Help");

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
                        //BoardUI.buildBoard();
                        break;
                    case "5":
                        logout();
                        break;
                    case "6":
                        quit = true;
                        break;
                    case "7":
                        help();
                        break;
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

    private void register() {
        System.out.println("Enter username: ");
        String username = scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.nextLine();
        System.out.println("Enter email: ");
        String email = scanner.nextLine();

        RegisterResponse response = serverFacade.register(new UserData(username, password, email));
        System.out.println("Registered. Welcome " + response.username() + "!\n");
        authToken = response.authToken();
        loggedIn = true;
    }

    private void login() {
        System.out.println("Enter username: ");
        String username = scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.nextLine();

        LoginResponse response = serverFacade.login(new LoginRequest(username, password));
        authToken = response.authToken();
        System.out.println("Logged in. Welcome " + username + "!\n");
        loggedIn = true;
    }

    private void createGame(){
        System.out.println("Enter gameName: ");
        String gameName = scanner.nextLine();

        CreateGameResponse response = serverFacade.createGame(new CreateGameRequest(gameName, authToken));
        System.out.println("Game created. Game ID is: " + response.gameID());
    }

    private void listGames() {
        ListGamesResponse response = serverFacade.listGames(new ListGamesRequest(authToken));
        Collection<SimpleGameData> games = response.games();
        for (SimpleGameData game: games){
            System.out.println("Game ID: " + game.gameID() + ",  White Username: " + game.whiteUsername() +
                    ",  Black Username: " + game.blackUsername() + ",  Game Name: " + game.gameName() + ";");
        }
        System.out.println();
    }

    private void joinGame() {
        String gameID = scanner.nextLine();
        System.out.println("Would you like to be: white/black?");
        String colorChoice = scanner.nextLine();
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        Boolean colorRepeat = true;
        while (colorRepeat) {
            if (colorChoice.equals("white")) {
                color = ChessGame.TeamColor.WHITE;
                colorRepeat = false;
            } else if (colorChoice.equals("black")) {
                color = ChessGame.TeamColor.BLACK;
                colorRepeat = false;
            } else {
                System.out.println("Invalid entry. Please enter one of the two options as shown.");
            }
        }
        serverFacade.joinGame(new JoinGameRequest(color, Integer.parseInt(gameID), authToken));
    }

    private void observeGame() {
        System.out.println("Which game do you want to observe?");
        String gameID = scanner.nextLine();
        serverFacade.observeGame(Integer.parseInt(gameID));
    }

    private void logout() {
        serverFacade.logout(authToken);
        System.out.println("Logged out. See ya later!\n");
        loggedIn = false;
    }

    private void help() {
        System.out.println("Help yourself! I've got enough to figure out here");
    }

    public String getAuth() {
        return authToken;
    }

}
