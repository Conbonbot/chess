package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData addAuthData(AuthData authData);
    AuthData getAuth(String authToken);
    void removeAuthData(AuthData authData);
    void clear();
}
