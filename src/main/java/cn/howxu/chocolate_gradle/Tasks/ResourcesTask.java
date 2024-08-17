package cn.howxu.chocolate_gradle.Tasks;

import cn.howxu.chocolate_gradle.util.net.DownloadUtil;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.net.URISyntaxException;

public class ResourcesTask extends DefaultTask {

    @TaskAction
    public void getRuntimeResources(){
        //getProject().getLogger().lifecycle("Hello");
        //System.out.println(getProject().getProjectDir().toString());
        try {

            DownloadUtil util = new DownloadUtil(getProject());
            //获取运行依赖
            util.downloadVersionJson();
            util.downloadLibraries();
            util.downloadSharedLibraries();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
