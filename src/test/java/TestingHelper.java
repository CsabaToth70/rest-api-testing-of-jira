import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestingHelper {
    private static final String rootPath = System.getProperty("user.dir") + "/";
    private static final String appConfigPath = rootPath + "app.properties";
    private static Properties appProps = new Properties();

    public static String getProperty(String userData) throws IOException {
        String userProperty = "";
        appProps.load(new FileInputStream(appConfigPath));
        if (userData.equals("username")) {
            userProperty = appProps.getProperty("username");
        } else if (userData.equals("password")) {
            userProperty = appProps.getProperty("password");
        } else {
            System.out.println("Wrong input");
        }
        return userProperty;
    }

}