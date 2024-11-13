package ui;

import chess.ChessGame;
import model.requestresult.*;
import network.ServerFacade;

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
        System.out.println("What would you like to do? (Select the number of the option you prefer)");

        while (!quit){
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
    }

    private void register() {
        System.out.println("Enter username: ");
        String username = scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.nextLine();
        System.out.println("Enter email: ");
        String email = scanner.nextLine();

        serverFacade.register(new RegisterResult(username, password, email));
        loggedIn = true;
    }

    private void login() {
        System.out.println("Enter username: ");
        String username = scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.nextLine();

        serverFacade.login(new LoginRequest(username, password));
        loggedIn = true;
    }

    private void createGame(){
        System.out.println("Enter gameName: ");
        String gameName = scanner.nextLine();

        serverFacade.createGame(new CreateGameRequest(gameName, authToken));
    }

    private void listGames() {
        serverFacade.listGames(new ListGamesRequest(authToken));
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
        loggedIn = false;
    }

    private void quit() {
        return;
    }

    private void help() {
        System.out.println("Help yourself! I've got enough to figure out here");
    }

}
