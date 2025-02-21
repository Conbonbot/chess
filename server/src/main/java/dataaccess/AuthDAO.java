package dataaccess;

import exception.ResponseException;
import model.AuthData;

public interface AuthDAO {
    AuthData addAuthData(AuthData authData);
    AuthData getAuth(String authToken) throws ResponseException;
    String generateAuth();
    void removeAuthData(AuthData authData);
    void clear();
}
