package dataaccess;
import model.UserData;
public interface UserDAO {
    UserData addUserData(UserData userData);
    UserData getUser(String username);
    void clear();
}
