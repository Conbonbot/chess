package dataaccess;

import java.util.ArrayList;
import java.util.UUID;

import exception.ResponseException;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {
    final private ArrayList<AuthData> authList = new ArrayList<>();

    @Override
    public AuthData addAuthData(AuthData authData){
        authData = new AuthData(authData.authToken(), authData.username());
        authList.add(authData);
        return authData;
    }

    @Override
    public AuthData getAuth(String authToken) throws ResponseException{
        for(AuthData authData : authList){
            if(authData.authToken().equals(authToken)){
                return authData;
            }
        }
        throw new ResponseException(401, "Error: unauthorized");
    }

    @Override
    public String generateAuth(){
        String auth = UUID.randomUUID().toString();
        for(AuthData authData : authList){
            if(authData.authToken().equals(auth)){
                auth = generateAuth();
            }
        }
        return auth;
    }

    @Override
    public void removeAuthData(AuthData authData){
        authList.remove(authData);
    }

    @Override
    public void clear(){
        authList.clear();
    }
}
