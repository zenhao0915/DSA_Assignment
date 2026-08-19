import java.util.HashSet;
import java.util.Objects;

record User(String userName, String password, boolean isAdmin) {
    public static HashSet<User> userDatabase = new HashSet<>();
    public static User currentUser = null;

    public static User getUserByName(String userName) {
        return userDatabase.stream().filter(u -> u.userName.equalsIgnoreCase(userName)).findFirst().orElse(null);
    }

    public boolean isUserExist() {
        return userDatabase.stream().anyMatch(u -> Objects.equals(u.userName.toLowerCase(), userName.toLowerCase()));
    }

    public boolean isValid() {
        return userDatabase.stream().anyMatch(u -> Objects.equals(u.userName.toLowerCase(), userName.toLowerCase()) && Objects.equals(u.password.toLowerCase(), password.toLowerCase()));
        //check if in user list the user has the correct name and password (valid user in userList)
    }

    public boolean isValidChar(String content) {
        return !content.contains("\\");
    }

    public boolean isUserAdmin() {
        boolean found = false;
        for (User u : userDatabase) {           //userName de getter
            if (u.isAdmin && Objects.equals(u.userName.toLowerCase(), userName().toLowerCase())) {
                found = true;
                break;   // so it won't check the rest of the userList
            }
        }
        return found;

    }
}
