package dataaccess;
import model.UserData;
public interface UserDAO {
    UserData addUserData(UserData userData) throws DataAccessException;
    UserData getUser(String username);
    void clear();
}
