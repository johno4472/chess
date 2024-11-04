package dataaccess;
import model.AuthData;

public interface AuthDAO {

    public void clear();

    public AuthData createAuth();

    public AuthData getAuth(String authToken);

    public void deleteAuth(AuthData authData);

}
