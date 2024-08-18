plugins {
    id("java")
    //本地发布用的插件
    id("maven-publish")
    //远程仓库插件发布
    id("com.gradle.plugin-publish") version "1.2.1"
    //id ("org.jetbrains.kotlin.jvm")
    //id ("java-gradle-plugin")
}

group = "cn.howxu.chocolate_gradle"
version = "1.1"



repositories {
    //maven { url = uri("https://maven.aliyun.com/repositories/central") }
    //mavenCentral()
    //google()
    mavenCentral()
    //maven { url = uri("https://maven.google.com/") }
    //插件发布仓库
    //maven {url = uri("https://plugins.gradle.org/m2/")}
}

dependencies {
    //Gradle的插件API
    implementation(gradleApi())
    //这两个依赖用来处理下载
    implementation("commons-io:commons-io:2.16.1")
    implementation("com.google.code.gson:gson:2.11.0")

    implementation("org.apache.commons:commons-lang3:3.16.0")

    //implementation("com.gradle.publish:plugin-publish-plugin:0.21.0")
}

//发布到gradle远程仓库中的配置 But it doesn't work
gradlePlugin {
    //设定个人信息
    website = "https://github.com/HowXu/chocolate_gradle"
    vcsUrl = "https://github.com/HowXu/chocolate_gradle"
    //插件描述
    plugins {
        create("Chocolate-gradle") {
            id = "cn.howxu.chocolate_gradle"
            displayName = "Chocolate gradle plugin"
            description = "A Minecraft plugin for Chocolate Client development"
            tags = listOf("minecraft","mcp")
            implementationClass = "cn.howxu.chocolate_gradle.PluginMain"
        }
    }
}

//本地发布 for test
publishing {
    repositories {
        maven {
            name = "Local"
            url = uri("${projectDir}/repo")
        }
    }
}

//UTF8中文支持
tasks.withType<JavaCompile>{
    options.encoding = "UTF-8"
}
