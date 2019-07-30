import com.alejandro.thebestplugin.AccountDatabaseManager;
import com.alejandro.thebestplugin.MainCommandExecutor;
import com.alejandro.thebestplugin.TheBestPlugin;
import mocks.StatementMock;
import net.dv8tion.jda.core.JDA;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MainCommandExecutor.class, Bukkit.class})
public class GeneralUnitTest {

    @Mock(name = "plugin")
    private TheBestPlugin pluginMock;

    @Mock
    private CommandSender commandSenderMock;

    @Mock
    private Command commandMock;

    @Mock
    private OfflinePlayer offlinePlayerMock;

    @Mock(name = "jda")
    private JDA jdaMock;

    @InjectMocks
    private MainCommandExecutor commandExecutorUnderTest;

    private YamlConfiguration config = new YamlConfiguration();

    // Initialize the yaml config
    {
        try {
            config.load(new File("C:\\Users\\alejo\\OneDrive\\Documents\\IntelliJ-workspace\\TheBestPlugin\\src\\main\\resources\\config.yml"));
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    //@Test
    public void registerAccountTest() {



        assertTrue(
                commandExecutorUnderTest.onCommand(commandSenderMock, commandMock, "", new String[] {
                        "342002891953012737",
                        "67fe62ad-2efd-4a2a-8ae5-98cc925116fc"
                })
        );
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void dataBaseConnectionTest() throws SQLException {

        assertNotNull(
                DriverManager.getConnection(
                        config.getString("db_url"),
                        config.getString("db_user"),
                        config.getString("db_pass")
                )
        );
    }

    @Test
    public void textChannelValidIDTest() {
        System.out.println(config.getLong("in_game_channel_id") + " : " + config.getLong("command_channel_id"));

        assertTrue(config.getLong("in_game_channel_id") != 0L);
        assertTrue(config.getLong("command_channel_id") != 0L);
    }

    @Test
    public void retrieveAccountsTest() {

        AccountDatabaseManager databaseManager = new AccountDatabaseManager(pluginMock, jdaMock);

        String[][] resultingData = databaseManager.retrieveAccountsFromDatabase(new StatementMock());

        assertTrue(Arrays.deepEquals(resultingData, new String[][]{{"Discord ID 1", "Player UUID 1"}, {"Discord ID 2", "Player UUID 2"}}));
    }
}