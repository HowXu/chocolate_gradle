package cn.howxu.chocolate_gradle.Tasks;

import cn.howxu.chocolate_gradle.util.build.buildArchive;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;

public class BuildArchive extends DefaultTask {

    @TaskAction
    public void buildArchive() throws IOException {
        new buildArchive(getProject()).build();
    }

}
