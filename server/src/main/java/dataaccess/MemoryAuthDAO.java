package dataaccess;

import model.AuthData;
import model.GameData;
import java.util.UUID;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {

    final private HashMap<String, AuthData> authList = new HashMap<>();

    @Override
    public void clear() {
        authList.clear();
    }

    @Override
    public String createAuth(String authToken, AuthData authData) {
        authList.put(authToken, authData);
        return authToken;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authList.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        authList.remove(authToken);
    }
}
