package dataaccess;
import model.AuthData;

public interface AuthDAO {

    public void clear();

    public String createAuth(String authToken, AuthData authData);

    public AuthData getAuth(String authToken);

    public void deleteAuth(String authToken);

}
