import com.alejandro.AccountMapSerializationHandler;
import com.alejandro.TheBestPlugin;
import com.google.common.collect.HashBiMap;
import org.junit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class AccountMapSerializationHandlerTest {

    private Connection SQLConnection;
    private HashBiMap<Long, String> testSerializedAccountsHashBiMap = HashBiMap.create();

    @BeforeClass
    public static void initializeTestEnvironment() throws SQLException {

        Connection SQLConnection = DriverManager.getConnection("jdbc:mysql://198.245.51.96:3306/db_63051", "db_63051", "dc06f6ce63");
        if ( SQLConnection != null )
            System.out.println("SQL CONNECTION ESTABLISHED");
    }

    @Test
    public void accountMapShouldSerialize() {

        AccountMapSerializationHandler testHandler =
                new AccountMapSerializationHandler(SQLConnection, TheBestPlugin.getInstance(), TheBestPlugin.getJDA());

        assertNotNull("Deserialized account map is empty!", testHandler.deserializeFromDatabase());
    }
}