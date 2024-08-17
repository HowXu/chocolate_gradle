package cn.howxu.chocolate_gradle.util.net;

import cn.howxu.chocolate_gradle.Tasks.ResourcesExtension;
import cn.howxu.chocolate_gradle.util.os.OS;
import cn.howxu.chocolate_gradle.util.os.OSCheck;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gradle.api.Project;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DownloadUtil {
    OS os = OSCheck.checkOS();
    String GameJsonStr = "";
    String GameJsonURI;
    JsonObject GameJson = new JsonObject();
    Gson gson = new Gson();
    String runDir = "";
    Project project;

    public DownloadUtil(Project target) throws URISyntaxException, IOException {
        //1.8.9的json数据
        GameJsonURI = "https://piston-meta.mojang.com/v1/packages/d546f1707a3f2b7d034eece5ea2e311eda875787/1.8.9.json";
        GameJsonStr = IOUtils.toString(new URI(GameJsonURI), StandardCharsets.UTF_8);
        GameJson = gson.fromJson(GameJsonStr, JsonObject.class);
        runDir = target.getProjectDir() + File.separator + "run";
        project = target;
    }

    public void downloadAssets() throws URISyntaxException, IOException {
        File indexesDIr = new File(runDir + File.separator + "assets" + File.separator + "indexes");
        File objectDIr = new File(runDir + File.separator + "assets" + File.separator + "objects");
        //File versionDIr = new File(runDir + File.separator + "assets" + File.separator + "objects");
        File VersionJsonFile = new File(runDir + File.separator + "assets" + File.separator + "indexes" + File.separator + "1.8.json");
        String VersionFileURI = "https://launchermeta.mojang.com/v1/packages/f6ad102bcaa53b1a58358f16e376d548d44933ec/1.8.json";
        String metaURI = "https://resources.download.minecraft.net/";
        if (!indexesDIr.exists()){
            indexesDIr.mkdir();
        }

        if (!objectDIr.exists()){
            objectDIr.mkdir();
        }

        //get https://launchermeta.mojang.com/v1/packages/f6ad102bcaa53b1a58358f16e376d548d44933ec/1.8.json
        JsonObject assetsJson = gson.fromJson(IOUtils.toString(new URI(GameJson.get("assetIndex").getAsJsonObject().get("url").getAsString()),StandardCharsets.UTF_8), JsonObject.class);
        //download

        //download 1.8.json
        if (!VersionJsonFile.exists()){
            FileUtils.writeByteArrayToFile(VersionJsonFile, IOUtils.toByteArray(new URL(VersionFileURI)));
        }

        //download assets
        project.getLogger().lifecycle("Start download assets...");
        for (Map.Entry<String, JsonElement> objects : assetsJson.get("objects").getAsJsonObject().entrySet()) {
            //not a json file
            AssetObject ChildNode = gson.fromJson(objects.getValue(), AssetObject.class);
            String hash = ChildNode.getHash();
            File toSave = new File(objectDIr.getPath() + File.separator + hash.substring(0,2) + File.separator + hash);
            if (!toSave.exists()){
                FileUtils.writeByteArrayToFile(toSave, IOUtils.toByteArray(new URL(metaURI + hash.substring(0,2) + "/" + hash)));
            }
        }
        project.getLogger().lifecycle("Finish download assets!");
    }

    public void downloadLibraries() throws IOException {
        for (JsonElement element : GameJson.get("libraries").getAsJsonArray()) {
            JsonObject downloadsJson = element.getAsJsonObject().get("downloads").getAsJsonObject();
            JsonElement artifact = downloadsJson.get("artifact");
            //有些Json词条是只有DLL下载(即没有artifact)，这里要判断
            if (artifact != null) {
                JsonObject artifactJson = artifact.getAsJsonObject();
                //System.out.println(artifactJson.get("url").getAsString());

                //下载文件
                String path = artifactJson.get("path").getAsString();
                if (os.getOs().equals("windows")) {
                    //windows下文件分割符
                    path = path.replace('/', '\\');
                }
                File FilePath = new File(runDir + File.separator + "libraries" + File.separator + path);
                if (!FilePath.exists()) {
                    //加上判定防止重复下载
                    String uri = artifactJson.get("url").getAsString();
                    project.getLogger().lifecycle("Library Download: " + uri);
                    FileUtils.writeByteArrayToFile(FilePath, IOUtils.toByteArray(new URL(uri)));
                }
                //TODO Hash Check
            }

        }
    }

    public void downloadNatives(boolean isForced) throws IOException {
        //natives-dir
        String NativesPath = runDir + File.separator + "versions" + File.separator + project.getRootProject().getName() + File.separator + project.getRootProject().getName() + "-natives";
        File NativesFolder = new File(NativesPath);
        if (!NativesFolder.exists()){
            NativesFolder.mkdir();
        }

        File CacheFolderCheck = new File(runDir + File.separator + "cache" + File.separator + "hash");
        if (!isForced){
            if (CacheFolderCheck.exists()){
                return;
            }
        }

        for (JsonElement element : GameJson.get("libraries").getAsJsonArray()) {
            JsonObject downloadsJson = element.getAsJsonObject().get("downloads").getAsJsonObject();
            JsonElement classifiers = downloadsJson.get("classifiers");
            //有些Json词条是只有DLL下载(即没有artifact)，这里要判断
            if (classifiers != null) {
                JsonObject classifiersJson = classifiers.getAsJsonObject();
                JsonElement classifiersNativesJsonElement = classifiersJson.get("natives-"+os.getOs());
                //due to natives-windows-64
                if (classifiersNativesJsonElement == null){
                    classifiersNativesJsonElement = classifiersJson.get("natives-"+os.getOs()+'-'+os.getArch());
                }
                JsonObject classifiersNativesJson = classifiersNativesJsonElement.getAsJsonObject();

                //下载文件
                String path = classifiersNativesJson.get("path").getAsString();
                if (os.getOs().equals("windows")) {
                    //windows下文件分割符
                    path = path.replace('/', '\\');
                }
                //Cache First
                File FilePath = new File(runDir + File.separator + "cache" + File.separator + path);
                //get URL
                String uri = classifiersNativesJson.get("url").getAsString();
                project.getLogger().lifecycle("Native Download: " + uri);
                //get file
                FileUtils.writeByteArrayToFile(FilePath, IOUtils.toByteArray(new URL(uri)));
                //TODO Hash Check
                //unzip
                JarFile jarFile = new JarFile(FilePath);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    if (jarEntry.isDirectory() || jarEntry.getName().contains("META-INF")) {
                        continue;
                    }
                    InputStream inputStream = jarFile.getInputStream(jarEntry);
                    FileUtils.writeByteArrayToFile(new File(NativesPath, jarEntry.getName()), IOUtils.toByteArray(inputStream));
                    inputStream.close();
                }
                jarFile.close();

                //delete cache
                FilePath.delete();
            }
        }
        if (!CacheFolderCheck.exists()){
            CacheFolderCheck.mkdir();
        }
    }

    public void downloadVersionJson() throws IOException, URISyntaxException {

        File toSaveFilePath = new File(runDir + File.separator + "versions" + File.separator + project.getRootProject().getName() + File.separator + project.getRootProject().getName() + ".json");
        if (!toSaveFilePath.exists()){
            //copy json file
            String GithubFile = "https://raw.githubusercontent.com/HowXu/Chocolate/main/Chocolate.json";
            project.getLogger().lifecycle("Version Json Download: " + GithubFile);
            FileUtils.writeByteArrayToFile(toSaveFilePath, IOUtils.toByteArray(new URL(GithubFile)));

        }
    }

}
