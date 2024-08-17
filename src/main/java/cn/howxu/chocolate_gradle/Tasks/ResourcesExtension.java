package cn.howxu.chocolate_gradle.Tasks;

import java.util.Random;

public class ResourcesExtension{
    public String username = "dev" + new Random().nextInt(999);
    public String mainClass = "tritium.launch.Main";
}
