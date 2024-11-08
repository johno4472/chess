package dataaccess;

import model.AuthData;

public class MySQLAuthDAO implements AuthDAO {
    @Override
    public void clear() {

    }

    @Override
    public String createAuth(String authToken, AuthData authData) {
        return "";
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }
}
