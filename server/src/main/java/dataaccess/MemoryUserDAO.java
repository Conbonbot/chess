package dataaccess;

import java.util.ArrayList;

import model.UserData;

public class MemoryUserDAO implements UserDAO{
    final private ArrayList<UserData> userList = new ArrayList<>();

    @Override
    public UserData addUserData(UserData userData){
        userList.add(userData);
        return userData;
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
        System.out.println("Clear | UserDAO");
        userList.clear();
    }
}
