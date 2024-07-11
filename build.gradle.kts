plugins {
    java
}

apply(plugin = "project")

subprojects {
    group = "core.demo"
    version = "1.0.0"

    repositories {
        maven {
            url = uri("https://neowu.github.io/maven-repo/")
            content {
                includeGroupByRegex("core\\.framework.*")
            }
        }
    }

    if (childProjects.isEmpty()) {
        sourceSets {
            create("dev") {
                java.srcDir("src/dev/java")
                compileClasspath += sourceSets["main"].runtimeClasspath
                runtimeClasspath += sourceSets["main"].runtimeClasspath
            }
        }
    }
}

val coreNGVersion = "9.0.9"
val hsqlVersion = "2.7.2"
val jacksonVersion = "2.17.0"

configure(subprojects.filter { it.name.endsWith("-db-migration") }) {
    apply(plugin = "db-migration")

    dependencies {
        runtimeOnly("com.mysql:mysql-connector-j:8.4.0")
        runtimeOnly("org.postgresql:postgresql:42.7.1")
    }
}

configure(subprojects.filter { it.name.endsWith("-es-migration") }) {
    apply(plugin = "app")
    dependencies {
        implementation("core.framework:core-ng:${coreNGVersion}")
        implementation("core.framework:core-ng-search:${coreNGVersion}")
    }
    tasks.register("esMigrate") {
        dependsOn("run")
    }
}

configure(subprojects.filter { it.name.endsWith("-mongo-migration") }) {
    apply(plugin = "app")
    dependencies {
        implementation("core.framework:core-ng:${coreNGVersion}")
        implementation("core.framework:core-ng-mongo:${coreNGVersion}")
    }
    tasks.register("mongoMigrate") {
        dependsOn("run")
    }
}

configure(subprojects.filter { it.name.endsWith("-service-interface") }) {
    dependencies {
        implementation("core.framework:core-ng-api:${coreNGVersion}")
    }
}

configure(listOf(project(":hello-service"))) {
    apply(plugin = "app")
    dependencies {
        implementation("core.framework:core-ng:${coreNGVersion}")
        testImplementation("core.framework:core-ng-test:${coreNGVersion}")
    }
}

project(":hello-service") {
    dependencies {
        implementation(project(":hello-service-interface"))
    }
}

