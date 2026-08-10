import java.util.*;

record User(String userName, String password, boolean isAdmin) {
    public static HashSet<User> userDatabase = new HashSet<>();
    public static User currentUser = null;

    public boolean isValid() {
        return userDatabase.stream().anyMatch(u -> Objects.equals(u.userName, userName) && Objects.equals(u.password, password));
        //check if in user list the user has the correct name and password (valid user in userList)
    }

    public static User getUserByName(String userName) {
        return userDatabase.stream().filter(u -> u.userName.equals(userName)).findFirst().orElse(null);
    }

    public boolean isUserAdmin(List<User> userList) {
        boolean found = false;
        for (User u : userList) {           //userName de getter
            if (u.isAdmin && Objects.equals(u.userName, userName())) {
                found = true;
                break;   // so it won't check the rest of the userList
            }
        }
        return found;

    }
}
