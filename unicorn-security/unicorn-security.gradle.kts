/*
 * This file was generated by the Gradle 'init' task.
 */

description = "权限管理模块"

java {
    withJavadocJar()
    registerFeature("captcha") {
        usingSourceSet(sourceSets["main"])
    }
    registerFeature("businessLog") {
        usingSourceSet(sourceSets["main"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

val sharedManifest = java.manifest {
    attributes(
        "Developer" to "lWoHvYe",
        "Created-By" to "Gradle",
        "Built-By" to System.getProperty("user.name"),
        "Build-Jdk-Spec" to System.getProperty("java.version"),
    )
}

tasks.jar {
    enabled = true
    manifest {
        from(sharedManifest)
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Automatic-Module-Name" to "lwohvye.${project.name.replace("-", ".")}"
        )
    }
    into("META-INF/maven/${project.group}/${project.name}") {
        from("generatePomFileForMavenJavaSecurityPublication")
        rename(".*", "pom.xml")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJavaSecurity") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Unicorn Security Control")
                description.set("Security module with Control and Management")
                url.set("https://github.com/lWoHvYe/unicorn.git")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("lWoHvYe")
                        name.set("王红岩(lWoHvYe)")
                        email.set("lWoHvYe@outlook.com")
                        url.set("https://www.lwohvye.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/lWoHvYe/unicorn.git")
                    developerConnection.set("scm:git:ssh://github.com/lWoHvYe/unicorn.git")
                    url.set("https://github.com/lWoHvYe/unicorn/tree/main")
                }
            }
        }
    }
}

dependencies {
    api(project(":unicorn-sys-api"))
    api("org.springframework.boot:spring-boot-starter-websocket")
    api(libs.jjwt.api)
    api(libs.jjwt.impl)
    api(libs.jjwt.jackson)
    api(libs.quartz)
    api(libs.easy.captcha)
    "captchaImplementation"(libs.captcha)
    api(libs.oshi.core)
    // It seems that xxxImplementation will work on runtimeClasspath for the customers while xxxApi compileClasspath
    "businessLogApi"(libs.bizlog)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}