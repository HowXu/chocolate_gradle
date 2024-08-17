package cn.howxu.chocolate_gradle.Tasks;

import cn.howxu.chocolate_gradle.util.launch.GameLaunch;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.net.URISyntaxException;

public class RunGame extends DefaultTask {

    @TaskAction
    public void RunGame() throws IOException, URISyntaxException {
        new GameLaunch(getProject()).runGame();
    }
}
