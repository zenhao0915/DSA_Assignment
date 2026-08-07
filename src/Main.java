import java.util.*;

public class Main {
    private static Graph graphManager = new Graph();
    private static RoutePlanner routePlanner = new RoutePlanner(Graph.graphMap);
    private static Scanner scanner = new Scanner(System.in);
    private static List<User> userDatabase = new ArrayList<>();

    public static void main(String[] args) {
        // 初始化一些测试账号 (可以根据需要修改)
        userDatabase.add(new User("admin", "admin123", true));
        userDatabase.add(new User("user", "user123", false));

        System.out.println("=======================================");
        System.out.println("  Welcome to Metro Management System   ");
        System.out.println("=======================================");

        // 1. 登录逻辑
        User loggedInUser = handleLogin();
        if (loggedInUser == null) {
            System.out.println("Exiting System. Goodbye!");
            return;
        }

        User.currentUser = loggedInUser;
        System.out.println("\nLogin Successfu  l! Welcome, " + loggedInUser.userName());

        // 2. 主菜单循环
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
    }

    // 处理登录
    private static User handleLogin() {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        for (User u : userDatabase) {
            if (u.userName().equals(username) && u.password().equals(password)) {
                return u;
            }
        }
        System.out.println("[Error] Invalid Username or Password!");
        return null;
    }

    // Admin 菜单
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

    // 普通 User 菜单
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

    // Admin 功能分支
    private static boolean handleAdminChoice(int choice) {
        switch (choice) {
            case 1 -> StationNetwork.startStationNetwork();
            case 2 -> routePlanner.RoutePlanning();
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

    // 普通 User 功能分支
    private static boolean handleUserChoice(int choice) {
        switch (choice) {
            case 1 -> StationNetwork.startStationNetwork();
            case 2 -> routePlanner.RoutePlanning();
            case 3 -> {
                System.out.println("Logging out... Goodbye!");
                return false;
            }
            default -> System.out.println("[Error] Invalid option! Try again.");
        }
        return true;
    }

    // --- 图操作交互输入 ---

    private static void handleAddVertex() {
        System.out.println("\n== Add New Station ==");
        System.out.print("Enter Station ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Station Name: ");
        String name = scanner.nextLine();
        System.out.print("Is Station Working? (true/false): ");
        boolean isWorking = Boolean.parseBoolean(scanner.nextLine());

        graphManager.addVertex(id, name, isWorking);
    }

    private static void handleAddEdge() {
        System.out.println("\n== Create Connection Between Stations ==");
        System.out.print("Enter Starting Station ID: ");
        String srcID = scanner.nextLine();
        System.out.print("Enter Destination Station ID: ");
        String destID = scanner.nextLine();
        System.out.print("Enter Journey Time (minutes): ");
        int time = Integer.parseInt(scanner.nextLine());

        graphManager.addEdge(srcID, destID, time);
    }

    private static void handleRemoveVertex() {
        System.out.println("\n== Delete Station ==");
        System.out.print("Enter Station ID to remove: ");
        String id = scanner.nextLine();

        graphManager.removeVertex(id);
    }

    private static void handleRemoveEdge() {
        System.out.println("\n== Delete Connection ==");
        System.out.print("Enter Starting Station ID: ");
        String srcID = scanner.nextLine();
        System.out.print("Enter Destination Station ID: ");
        String destID = scanner.nextLine();

        graphManager.removeEdge(srcID, destID);
    }

    private static void handleUpdateStatus() {
        System.out.println("\n== Update Station Status ==");
        System.out.print("Enter Station ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter New Status (true for Working / false for Maintenance): ");
        boolean isWorking = Boolean.parseBoolean(scanner.nextLine());

        graphManager.updateStatus(id, isWorking);
    }
}