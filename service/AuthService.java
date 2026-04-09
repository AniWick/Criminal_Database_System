package service;

import database.CriminalDatabase;
import model.User;

public class AuthService {

    public boolean login(String username, String password) {

        User user = CriminalDatabase.getUser(username);

        if(user == null)
            return false;

        return user.getPassword().equals(password);
    }
}