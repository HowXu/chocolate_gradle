package cn.howxu.chocolate_gradle.util.build;

import cn.howxu.chocolate_gradle.Tasks.ResourcesExtension;
import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class buildArchive {
    Project project;
    String version;
    String artifact;
    File rootDir;
    String buildDir;
    File outputFile;
    ResourcesExtension extension;

    public buildArchive(Project target) {
        project = target;
        version = target.getVersion().toString();
        //System.out.println(version);
        artifact = target.getRootProject().getName();
        rootDir = target.getProjectDir();
        buildDir = target.getProjectDir() + File.separator + "build";
        outputFile = new File(rootDir.getPath() + File.separator + "build" + File.separator + "libs" + File.separator + artifact + "-" + version + ".jar");
        extension = project.getExtensions().getByType(ResourcesExtension.class);
    }

    public void build() throws IOException {
        //copy versionJson and JarFile

        File CacheFolder = new File(buildDir + File.separator + "cache" + File.separator + artifact);
        if (!CacheFolder.exists()) {
            CacheFolder.mkdir();
        }

        File archiveJarFile = new File(CacheFolder + File.separator + artifact + ".jar");
        File archiveJsonFile = new File(CacheFolder + File.separator + artifact + ".json");

        File versionJsonFile = new File(rootDir.getPath() + File.separator + "run" + File.separator + "versions" + File.separator + artifact + File.separator + artifact + ".json");

        FileUtils.copyFile(outputFile, archiveJarFile);
        FileUtils.copyFile(versionJsonFile, archiveJsonFile);

        //make archive file
        File Archive = new File(buildDir + File.separator + "cache" + File.separator + artifact + ".zip");
        compress(CacheFolder.getPath(), Archive.getPath());

        //delete
        archiveJarFile.delete();
        archiveJsonFile.delete();
    }


    private int BUFFER = 1024 * 8;


    /*
     * Based on https://blog.csdn.net/qq_42298793/article/details/107194668
     *
     *
     */
    public void compress(String fromPath, String toPath) throws IOException {
        File fromFile = new File(fromPath);
        File toFile = new File(toPath);
        if (!fromFile.exists()) {
            throw new FileNotFoundException(fromPath + "不存在！");
        }
        try (
                FileOutputStream outputStream = new FileOutputStream(toFile);
                CheckedOutputStream checkedOutputStream = new CheckedOutputStream(outputStream, new CRC32());
                ZipOutputStream zipOutputStream = new ZipOutputStream(checkedOutputStream)
        ) {
            String baseDir = "";
            compress(fromFile, zipOutputStream, baseDir);
        }
    }

    private void compress(File file, ZipOutputStream zipOut, String baseDir) throws IOException {
        if (file.isDirectory()) {
            compressDirectory(file, zipOut, baseDir);
        } else {
            compressFile(file, zipOut, baseDir);
        }
    }

    private void compressDirectory(File dir, ZipOutputStream zipOut, String baseDir) throws IOException {
        File[] files = dir.listFiles();
        if (files != null && ArrayUtils.isNotEmpty(files)) {
            for (File file : files) {
                compress(file, zipOut, baseDir + dir.getName() + "/");
            }
        }
    }

    private void compressFile(File file, ZipOutputStream zipOut, String baseDir) throws IOException {
        if (!file.exists()) {
            return;
        }
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            ZipEntry entry = new ZipEntry(baseDir + file.getName());
            zipOut.putNextEntry(entry);
            int count;
            byte[] data = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                zipOut.write(data, 0, count);
            }
        }
    }

}


