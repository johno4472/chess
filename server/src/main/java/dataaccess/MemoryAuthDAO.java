package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {

    final private HashMap<String, AuthData> authList = new HashMap<>();

    @Override
    public void clear() {

    }

    @Override
    public AuthData createAuth() {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authList.get(authToken);
    }

    @Override
    public void deleteAuth(AuthData authData) {
        authList.remove(authData.authToken());
    }
}
