package ui;

import java.io.IOException;
import java.util.Scanner;

public class consoleMenu {
    private boolean loggedIn;
    private ServerFacade serverFacade;
    Scanner scanner;

    consoleMenu() {
        System.out.println("Welcome to 240 Chess!!");
        loggedIn = false;
        serverFacade = new ServerFacade();
        scanner = new Scanner(System.in);

        runConsole();
    }

    private void runConsole() {
        System.out.println("What would you like to do? (Select the number of the option you prefer)");
        if (loggedIn) {
            runLoggedInMenu();
        }
        else {
            runStarterMenu();
        }
    }

    private void runStarterMenu() {
        System.out.println("1. Register\n2. Login\n3. Quit\n4. Help\n");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                register();
            case "2":
                login();
            case "3":
                quit();
            case "4":
                help();
        }
    }

    public void runLoggedInMenu() {
        System.out.println("1. Create Game\n2. List Games\n3. Join Game\n4. Observe Game\n5. Logout\n6. Quit\n7. Help");
        try {
            String choice = scanner.nextLine();
            switch (choice){
                case "1":
                    createGame();
                case "2":
                    listGames();
                case "3":
                    joinGame();
                case "4":
                    observeGame();
                case "5":
                    serverFacade.logout();
                case "6":
                    quit();
                case "7":
                    help();
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private void register() {
        System.out.println("Enter username: ");

        String username = scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.nextLine();
        System.out.println("Enter email: ");
        String email = scanner.nextLine();

        serverFacade.register(new Register);



    }

}
