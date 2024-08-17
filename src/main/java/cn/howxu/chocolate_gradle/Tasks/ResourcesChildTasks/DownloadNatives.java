package cn.howxu.chocolate_gradle.Tasks.ResourcesChildTasks;

import cn.howxu.chocolate_gradle.util.net.DownloadUtil;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.net.URISyntaxException;

public class DownloadNatives extends DefaultTask {
    @TaskAction
    public void getNativesResources() {
        try {
            DownloadUtil util = new DownloadUtil(getProject());
            util.downloadNatives(true);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
