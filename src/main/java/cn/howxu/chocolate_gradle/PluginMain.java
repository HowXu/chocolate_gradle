package cn.howxu.chocolate_gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class PluginMain implements Plugin<Project>{
    @Override
    public void apply(Project target) {
        target.getLogger().info("Hello Gradle");
        System.out.println("Hello Gradle");
    }
}
