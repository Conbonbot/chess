package dataaccess;

import java.util.ArrayList;

import exception.ResponseException;
import model.UserData;

public class MemoryUserDAO implements UserDAO{
    final private ArrayList<UserData> userList = new ArrayList<>();

    @Override
    public UserData addUserData(UserData userData) throws ResponseException{
        if(getUser(userData.username()) == null){
            userList.add(userData);
            return userData;
        }
        throw new ResponseException(403, "Error: already taken");
    }

    @Override
    public UserData getUser(String username){
        for(UserData user : userList){
            if(user.username().equals(username)){
                return user;
            }
        }
        return null;
    }

    @Override
    public void clear(){
        userList.clear();
    }
}
