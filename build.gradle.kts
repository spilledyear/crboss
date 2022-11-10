plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.9.0"
}

group = "com.lxy.tools.crboss"
version = "1.0"

repositories {
    mavenLocal()
// 依赖使用阿里云 maven 源
    maven {
        setUrl("https://maven.aliyun.com/repository/public/")
        setAllowInsecureProtocol(false)
    }
    maven {
        setUrl("https://maven.aliyun.com/repository/spring/")
        setAllowInsecureProtocol(false)
    }
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.2.3")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("213")
        untilBuild.set("223.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
