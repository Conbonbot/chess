package service;
import requests.Request;
import results.Result;
import dataaccess.UserDAO;

public class UserService {

    private final UserDAO userAccess;

    public UserService(UserDAO userAccess){
        this.userAccess = userAccess;
    }

    public Result.Register register(Request.Register registerRequest) {

        return new Result.Register(null, null, null);
    }

    public Result.Login login(Request.Login loginRequest){

        return new Result.Login(null, null);
    }

    public void logout(Request.Logout logoutRequest){

    }
}
