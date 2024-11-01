package dataaccess;
import model.UserData;

public interface UserDAO {

    public void clear();

    public void addUser(UserData user);

    public UserData getUser(String username);
}
