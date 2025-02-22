package dataaccess;

import exception.ResponseException;
import model.AuthData;

public interface AuthDAO {
    AuthData addAuthData(AuthData authData) throws ResponseException;
    AuthData getAuth(String authToken) throws ResponseException;
    void removeAuthData(AuthData authData) throws ResponseException;
    void clear() throws ResponseException;
    boolean verifyUser(String password, String providedClearTextPassword);
}
