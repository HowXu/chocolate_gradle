package cn.howxu.chocolate_gradle;

import cn.howxu.chocolate_gradle.Tasks.BuildArchive;
import cn.howxu.chocolate_gradle.Tasks.ResourcesChildTasks.DownloadNatives;
import cn.howxu.chocolate_gradle.Tasks.ResourcesExtension;
import cn.howxu.chocolate_gradle.Tasks.ResourcesTask;
import cn.howxu.chocolate_gradle.Tasks.RunGame;
import cn.howxu.chocolate_gradle.util.os.OS;
import cn.howxu.chocolate_gradle.util.os.OSCheck;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.HashMap;

public class PluginMain implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        //根据操作系统处理twitch依赖
        setDependencies(target);
        taskRegistry(target);
        extensionRegistry(target);

        target.getTasks().getByName("runClient").dependsOn("build","getRuntimeResources");
        target.getTasks().getByName("buildArch").dependsOn("build","getRuntimeResources");

    }

    public void extensionRegistry(Project target) {

        Class[] Extensions = new Class[]{ResourcesExtension.class};

        for (Class extension : Extensions) {
            target.getExtensions().create("chocolate",extension);
        }


    }

    public void taskRegistry(Project target) {

        HashMap<String, Class> Tasks = new HashMap<>();
        //任务
        Tasks.put("getRuntimeResources", ResourcesTask.class);
        Tasks.put("getNativesResources", DownloadNatives.class);
        Tasks.put("runClient", RunGame.class);
        Tasks.put("buildArch", BuildArchive.class);
        //注册
        Tasks.forEach((name, task) -> {
            target.getTasks().create(name, task, _task -> {
                _task.setGroup("Chocolate");
            });
        });

        //设置依赖
        //target.getTasks().getByName("getRuntimeResources").dependsOn("");
    }


    public void setDependencies(Project target) {

        String[] Dependencies = new String[]{
                ("com.mojang:netty:1.8.8"),
                ("oshi-project:oshi-core:1.1"),
                ("net.java.dev.jna:jna:3.4.0"),
                ("net.java.dev.jna:platform:3.4.0"),
                ("com.ibm.icu:icu4j-core-mojang:51.2"),
                ("net.sf.jopt-simple:jopt-simple:4.6"),
                ("com.paulscode:codecjorbis:20101023"),
                ("com.paulscode:codecwav:20101023"),
                ("com.paulscode:libraryjavasound:20101123"),
                ("com.paulscode:librarylwjglopenal:20100824"),
                ("com.paulscode:soundsystem:20120107"),
                ("io.netty:netty-all:4.0.23.Final"),
                ("com.google.guava:guava:17.0"),
                ("org.apache.commons:commons-lang3:3.3.2"),
                ("commons-io:commons-io:2.4"),
                ("commons-codec:commons-codec:1.9"),
                ("net.java.jinput:jinput:2.0.5"),
                ("net.java.jutils:jutils:1.0.0"),
                ("com.google.code.gson:gson:2.2.4"),
                ("com.mojang:authlib:1.5.21"),
                ("com.mojang:realms:1.7.59"),
                ("org.apache.commons:commons-compress:1.8.1"),
                ("org.apache.httpcomponents:httpclient:4.3.3"),
                ("commons-logging:commons-logging:1.1.3"),
                ("org.apache.httpcomponents:httpcore:4.3.2"),
                ("org.apache.logging.log4j:log4j-api:2.0-beta9"),
                ("org.apache.logging.log4j:log4j-core:2.0-beta9"),
                ("org.lwjgl.lwjgl:lwjgl:2.9.4-nightly-20150209"),
                ("org.lwjgl.lwjgl:lwjgl_util:2.9.4-nightly-20150209"),
                ("org.lwjgl.lwjgl:lwjgl-platform:2.9.4-nightly-20150209"),
                ("net.java.jinput:jinput-platform:2.0.5"),
                ("tv.twitch:twitch:6.5")
        };

        //添加依赖
        for (String dependency : Dependencies) {
            target.getDependencies().add("implementation", dependency);
        }


        OS os = OSCheck.checkOS();
        //tv_twitch_platform依赖处理
        String tv_twitch_platform = "tv.twitch:twitch-platform:6.5:natives";
        if (os.getOs().equals("windows")) {
            tv_twitch_platform = tv_twitch_platform + "-" + os.getOs() + "-" + os.getArch();
        } else if (os.getOs().equals("osx")) {
            tv_twitch_platform = tv_twitch_platform  + "-" + os.getOs();
        }
        target.getDependencies().add("implementation", tv_twitch_platform);

        //tv_twitch_external_platform依赖处理
        String tv_twitch_external_platform = "tv.twitch:twitch-external-platform:4.5:natives";
        if (os.getOs().equals("windows")) {
            target.getDependencies().add("implementation", tv_twitch_external_platform + "-" + os.getOs() + "-" + os.getArch());
        }


    }
}