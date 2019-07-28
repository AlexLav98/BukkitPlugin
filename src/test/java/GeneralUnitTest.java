import com.alejandro.MainCommandExecutor;
import com.alejandro.TheBestPlugin;
import com.google.common.collect.HashBiMap;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

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

    @Mock
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

    @Test
    public void registerAccountTest() {

        HashBiMap<Long, OfflinePlayer> instance = spy(HashBiMap.create());

        PowerMockito.mockStatic(Bukkit.class);

        when(commandMock.getName()).thenReturn("register-account");
        when(pluginMock.getLinkedAccountsMap()).thenReturn(instance);
        when(pluginMock.getJDA()).thenReturn(jdaMock);
        when(jdaMock.getUserById(any(Long.class))).thenReturn(mock(User.class));
        PowerMockito.when(Bukkit.getOfflinePlayer(any(UUID.class))).thenReturn(offlinePlayerMock);

        assertTrue(
                commandExecutorUnderTest.onCommand(commandSenderMock, commandMock, "", new String[] {
                        "342002891953012737",
                        "67fe62ad-2efd-4a2a-8ae5-98cc925116fc"
                })
        );

        verify(instance, only()).put(anyLong(), any(OfflinePlayer.class));

        assertTrue(instance.containsKey(342002891953012737L));
        assertTrue(instance.containsValue(offlinePlayerMock));
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
}