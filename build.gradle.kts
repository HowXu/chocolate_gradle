plugins {
    id("java")
    id("maven-publish")
    //id ("org.jetbrains.kotlin.jvm")
    //id ("java-gradle-plugin")
}

group = "cn.howxu"
version = "1.0"

repositories {
    //maven { url = uri("https://maven.aliyun.com/repositories/central") }
    //mavenCentral()
    //google()
    mavenCentral()
    //maven { url = uri("https://maven.google.com/") }
    //maven { url = uri("https://dl.google.com/dl/android/maven2/") }
}

dependencies {
    // 添加Gradle插件API
    //implementation("com.android.tools.build:gradle-api:8.5.0")
    implementation(gradleApi())
    //gradleApi()

}
