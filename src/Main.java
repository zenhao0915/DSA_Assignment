import java.util.Scanner;

public class Main {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String BOLD = "\u001B[1m";
    private static final Graph graphManager = new Graph();
    private static final RoutePlanner routePlanner = new RoutePlanner();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        User.userDatabase = FileManager.INSTANCE.loadUsers(); //let the data 互通
        Graph.graphMap = FileManager.INSTANCE.loadGraphFromFile();

        if (User.userDatabase.isEmpty()) {
            User.userDatabase.add(new User("admin", "admin123", true));
            User.userDatabase.add(new User("user", "user123", false));
        }
        System.out.println("=================================================");
        System.out.println("    Welcome to Generic Train Management System   ");
        System.out.println("=================================================");

        User loggedInUser = handleLogin();
        if (loggedInUser == null) {
            System.out.println("Exiting System. Goodbye!");
            return;
        }

        User.currentUser = loggedInUser;
        FileManager.INSTANCE.saveUsers(User.userDatabase);
        System.out.println("\nLogin Successful! Welcome, " + loggedInUser.userName());

        boolean running = true;
        while (running) {
            if (loggedInUser.isAdmin()) {
                showAdminMenu();
                int choice = getUserChoice();
                running = handleAdminChoice(choice);
            } else {
                showUserMenu();
                int choice = getUserChoice();
                running = handleUserChoice(choice);
            }
        }

        FileManager.INSTANCE.saveUsers(User.userDatabase);
    }

    // 处理登录
    private static User handleLogin() {
        String username = "", password;
        User tempUser = null;
        while (true) {
            System.out.println("""
                    1. Register User
                    2. Login User
                    3. Exit
                    """);
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear Buffer
            if (choice < 0 || choice > 3) {
                System.out.println("Invalid choice. Please try again.");
                return null;
            }
            switch (choice) {
                case 1, 2: {
                    System.out.print("Enter Username: ");
                    username = scanner.nextLine().trim();
                    System.out.print("Enter Password: ");
                    password = scanner.nextLine().trim();
                    User adminCheckUser = User.getUserByName(username);
                    tempUser = new User(username, password, adminCheckUser != null && adminCheckUser.isAdmin());
                    break;
                }
                default: {
                    System.out.println("System Exiting...");
                    FileManager.INSTANCE.saveUsers(User.userDatabase);
                    System.exit(0);
                    break;
                }
            }

            if (choice == 1) {
                if (!tempUser.isValidChar(username)) {
                    System.out.println("[Error] Invalid Username Characters! Please try again.");
                } else if (tempUser.isUserExist()) {
                    System.out.println("[Error] Username Exist! Please try again.");
                } else {
                    User.userDatabase.add(tempUser);
                }
            }
            if (tempUser.isValid()) break;
            else System.out.println("[Error] Invalid Username or Password!\n");
        }
        return tempUser;
    }

    private static void showAdminMenu() {
        System.out.println("\n========== ADMIN MENU ==========");
        System.out.println("1. View Station Network");
        System.out.println("2. Plan Route");
        System.out.println("3. Add Station");
        System.out.println("4. Add Connection (Edge)");
        System.out.println("5. Remove Station");
        System.out.println("6. Remove Connection (Edge)");
        System.out.println("7. Update Station Status");
        System.out.println("8. Exit");
        System.out.print("Choose an option: ");
    }

    private static void showUserMenu() {
        System.out.println("\n========== USER MENU ==========");
        System.out.println("1. View Station Network");
        System.out.println("2. Plan Route");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static boolean handleAdminChoice(int choice) {
        switch (choice) {
            case 1 -> StationNetwork.startStationNetwork();
            case 2 -> routePlanner.routePlanning(Graph.graphMap);
            case 3 -> handleAddVertex();
            case 4 -> handleAddEdge();
            case 5 -> handleRemoveVertex();
            case 6 -> handleRemoveEdge();
            case 7 -> handleUpdateStatus();
            case 8 -> {
                System.out.println("Logging out... Goodbye!");
                return false;
            }
            default -> System.out.println("[Error] Invalid option! Try again.");
        }
        return true;
    }

    private static boolean handleUserChoice(int choice) {
        switch (choice) {
            case 1 -> StationNetwork.startStationNetwork();
            case 2 -> routePlanner.routePlanning(Graph.graphMap);
            case 3 -> {
                System.out.println("Logging out... Goodbye!");
                return false;
            }
            default -> System.out.println("[Error] Invalid option! Try again.");
        }
        return true;
    }

    private static void handleAddVertex() {
        System.out.println("\n== Add New Station ==");
        System.out.print("Enter Station ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Station Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Is Station Working? (true/false): ");
        boolean isWorking = Boolean.parseBoolean(scanner.nextLine());

        graphManager.addVertex(id, name, isWorking);
    }

    private static void handleAddEdge() {
        System.out.println("\n== Create Connection Between Stations ==");
        System.out.print("Enter Starting Station ID: ");
        String srcID = scanner.nextLine().trim();
        System.out.print("Enter Destination Station ID: ");
        String destID = scanner.nextLine().trim();
        System.out.print("Enter Journey Time (minutes): ");
        int time = Integer.parseInt(scanner.nextLine());

        graphManager.addEdge(srcID, destID, time);
    }

    private static void handleRemoveVertex() {
        System.out.println("\n== Delete Station ==");
        System.out.print("Enter Station ID to remove: ");
        String id = scanner.nextLine().trim();

        graphManager.removeVertex(id);
    }

    private static void handleRemoveEdge() {
        System.out.println("\n== Delete Connection ==");
        System.out.print("Enter Starting Station ID: ");
        String srcID = scanner.nextLine().trim();
        System.out.print("Enter Destination Station ID: ");
        String destID = scanner.nextLine().trim();

        graphManager.removeEdge(srcID, destID);
    }

    private static void handleUpdateStatus() {
        System.out.println("\n== Update Station Status ==");
        System.out.print("Enter Station ID: ");
        String id = scanner.nextLine().trim();
        String newStatus;
        while (true) {
            System.out.print("Enter New Status (" + Main.GREEN + "true" + Main.RESET + " for Active / " + Main.RED + "false" + Main.RESET + " for Maintenance): ");
            newStatus = scanner.nextLine().trim();
            if (newStatus.equalsIgnoreCase("true") || newStatus.equalsIgnoreCase("false")) break;
            System.out.println("[Error] Invalid Input! Try again.");
        }
        boolean isWorking = Boolean.parseBoolean(newStatus);

        graphManager.updateStatus(id, isWorking);
    }
}