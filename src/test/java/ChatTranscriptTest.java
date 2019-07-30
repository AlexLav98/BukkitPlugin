import com.alejandro.thebestplugin.MainListenerWrapper;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit test focused on the chat transcript system.
 * Will test if Minecraft chat is sent to Discord.
 * Will test if Discord chat is sent to Minecraft.
 */
@RunWith(MockitoJUnitRunner.class)
public class ChatTranscriptTest {

    @Mock(name = "plugin", answer = Answers.RETURNS_MOCKS)
    private JavaPlugin javaPluginMock;

    @Mock(name = "jda", answer = Answers.RETURNS_MOCKS)
    private JDA jdaMock;

    @Mock(answer = Answers.RETURNS_MOCKS)
    TextChannel textChannelMock;

    @InjectMocks
    private MainListenerWrapper mainListenerWrapper;

    @Test
    public void nothing() {

    }
}