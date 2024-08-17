package cn.howxu.chocolate_gradle.util.launch;

import cn.howxu.chocolate_gradle.Tasks.ResourcesExtension;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.ant.AntProjectPropertiesDelegate;
import org.gradle.api.Project;
import org.gradle.tooling.model.gradle.GradleBuild;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.gradle.api.logging.Logger;

public class GameLaunch {
    Project project;
    String version;
    String artifact;
    File rootDir;
    String rundir;
    File outputFile;
    ResourcesExtension extension;

    public GameLaunch(Project target) {
        project = target;
        version = target.getVersion().toString();
        //System.out.println(version);
        artifact = target.getRootProject().getName();
        rootDir = target.getProjectDir();
        rundir = target.getProjectDir() + File.separator + "run";
        outputFile = new File(rootDir.getPath() + File.separator + "build" + File.separator + "libs" + File.separator + artifact + "-" + version + ".jar");
        extension = project.getExtensions().getByType(ResourcesExtension.class);
    }

    public void runGame() throws IOException, URISyntaxException {
        //copy jar to version folder
        File execFile = new File(rootDir + File.separator + "run" + File.separator + "versions" + File.separator + artifact + File.separator + artifact + ".jar");
        if (execFile.exists()) {
            if (!execFile.delete()) {
                project.getLogger().error("The file is occupied, please close the relevant process!");
            }
        }
        FileUtils.copyFile(outputFile,execFile);

        //copy json file
        File toSaveFilePath = new File(rootDir + File.separator + "run" + File.separator + "versions" + File.separator + artifact + File.separator + artifact + ".json");
        if (!toSaveFilePath.exists()) {
            String GithubFile = "https://raw.githubusercontent.com/HowXu/Chocolate/main/Chocolate.json";
            project.getLogger().lifecycle("Version Json Download: " + GithubFile);
            FileUtils.writeByteArrayToFile(toSaveFilePath, IOUtils.toByteArray(new URL(GithubFile)));
        }

        //get run args
        String name = "ChocolateGradle";
        String java = System.getProperty("java.home");
        String gameVersion = "1.8";
        //rootDir + File.separator + "run" + File.separator + "versions" + File.separator + artifact + File.separator + artifact + ".json"
        String jvmArgs = " -XX:+UseG1GC -XX:-UseAdaptiveSizePolicy -XX:-OmitStackTraceInFastThrow -Dfml.ignoreInvalidMinecraftCertificates=True -Dfml.ignorePatchDiscrepancies=True -Dlog4j2.formatMsgNoLookups=true -XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump -Xmn1024m -Xmx4096m ";
        JsonObject GameJson = (new Gson()).fromJson(IOUtils.toString(new URI("https://piston-meta.mojang.com/v1/packages/d546f1707a3f2b7d034eece5ea2e311eda875787/1.8.9.json"), StandardCharsets.UTF_8), JsonObject.class);
        String mainClass = extension.mainClass;
        String username = project.getExtensions().getByType(ResourcesExtension.class).username;
        String titleVersion = version;

        File assetsDir = new File(rundir, "assets");
        String nativeDir = rundir + File.separator + "versions" + File.separator + artifact + File.separator + artifact + "-" + "natives";
        StringBuilder libraries = new StringBuilder();
        libraries.append('"');

        for (JsonElement element : GameJson.get("libraries").getAsJsonArray()) {
            JsonObject downloadsJson = element.getAsJsonObject().get("downloads").getAsJsonObject();
            JsonElement artifactEle = downloadsJson.get("artifact");
            if (artifactEle != null) {
                JsonObject artifactJson = artifactEle.getAsJsonObject();
                //Build
                String path = artifactJson.get("path").getAsString();
                libraries.append(rundir + File.separator + "libraries" + File.separator).append(path).append(";");
            }
        }
        libraries.append(execFile.getPath()).append('"');


        String uuid = "0";
        String accessToken = "0";
        String args = java + File.separator+  "bin"+ File.separator +"java.exe " + jvmArgs + " -Djava.library.path=" + nativeDir + " -Dminecraft.launcher.brand="
                + name + " -Dminecraft.launcher.version=" + version + " -cp " + libraries + " " + mainClass
                + " --username " + username + " --version " + titleVersion +
                " --gameDir " + rundir + " --assetsDir " + assetsDir + " --assetIndex " + gameVersion
                + " --uuid " + uuid + " --accessToken " + accessToken + " --userProperties {} --userType msa"
                + "--width 854 --height 480";

        //launch game
        Process exec = Runtime.getRuntime().exec(args);
        // read logs both keep program alive
        BufferedReader br = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        String line;
        Logger logger = project.getLogger();
        while ((line = br.readLine()) != null) {
            logger.lifecycle(line);
        }
    }

}
