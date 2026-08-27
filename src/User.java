import java.util.HashSet;
import java.util.Objects;

record User(String userName, String password, boolean isAdmin) {
    public static HashSet<User> userDatabase = new HashSet<>();
    public static User currentUser = null;

    public static User getUserByName(String userName) {
        if (userName == null) return null;
        return userDatabase.stream()
                .filter(u -> u.userName() != null && u.userName().equalsIgnoreCase(userName))
                .findFirst()
                .orElse(null);
    }

    public boolean isUserExist() {
        if (userName == null) return false;
        return userDatabase.stream()
                .anyMatch(u -> u.userName() != null && u.userName().equalsIgnoreCase(userName));
    }

    public boolean isValid() {
        if (userName == null || password == null) return false;
        return userDatabase.stream()
                .anyMatch(u -> u.userName() != null && u.userName().equalsIgnoreCase(userName) && Objects.equals(u.password(), password));
    }

    public boolean isValidChar(String content) {
        return content != null && !content.contains("\\") && !content.isEmpty();
    }

    public boolean isUserAdmin() {
        if (userName == null) return false;
        return userDatabase.stream() //userName de getter
                .anyMatch(u -> u.isAdmin() && u.userName() != null && u.userName().equalsIgnoreCase(userName));
    }
}