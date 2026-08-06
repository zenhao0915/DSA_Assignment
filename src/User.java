import java.util.List;
import java.util.Objects;

record User(String userName, String password, boolean isAdmin) {

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

    public boolean isPasswordCorrect(List<User> userList) {
        boolean found = false;
        for (User u : userList) {           //userName de getter
            if (Objects.equals(u.password,password()) && Objects.equals(u.userName, userName())) {
                found = true;
                break;
            }
        }

        return found;

    }


}
