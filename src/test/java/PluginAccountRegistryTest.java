import com.alejandro.PluginAccountRegistry;
import com.alejandro.TheBestPlugin;
import net.dv8tion.jda.core.JDA;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PluginAccountRegistryTest {

    @Parameter
    private String[][] data;

    @Mock(answer = Answers.RETURNS_MOCKS)
    TheBestPlugin pluginMock;

    @Mock(answer = Answers.RETURNS_MOCKS)
    JDA jdaMock;

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);
    }

    @Parameters
    public static List<String[][]> data() {

        String[][][] data = {
                {{"111111111111111111", "1234-1234-1234-1234"}},
                {{"222222222222222222", "2345-2345-2345-2345"}},
                {{"333333333333333333", "3456-3456-3456-3456"}},
                {{"444444444444444444", "4567-4567-4567-4567"}}
        };

        return Arrays.asList(data);
    }

    @Test
    public void registrySerializationTest() {

        PluginAccountRegistry registryUnderTest = new PluginAccountRegistry(data, pluginMock, jdaMock);
    }
}
