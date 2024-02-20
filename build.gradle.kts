import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.forge.gradle)
}

group = libs.versions.mod.group.get()
version = libs.versions.mod.version.get()
base.archivesName = libs.versions.mod.name.get()

println(
    "Java: ${System.getProperty("java.version")}, " +
            "JVM: ${System.getProperty("java.vm.version")} (${System.getProperty("java.vendor")}), " +
            "Arch: ${System.getProperty("os.arch")}"
)
minecraft {
    mappings(libs.versions.mappings.channel, libs.versions.mappings.version)
    copyIdeResources = true

    runs {
        configureEach {
            workingDirectory(project.file("run"))

            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", libs.versions.mod.id.get())

            mods {
                create(libs.versions.mod.id.get()) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("client") {

        }

        create("server") {
            args("--nogui")
        }
    }
}

dependencies {
    minecraft(libs.minecraft)
}

tasks {
    processResources.configure {
        val propertiesMap: Map<String, String> = hashMapOf(
            "forge_version" to libs.versions.forge.asProvider(),
            "forge_version_range" to libs.versions.forge.range,
            "loader_version_range" to libs.versions.loader.range,
            "minecraft_version" to libs.versions.mc.asProvider(),
            "minecraft_version_range" to libs.versions.mc.range,
            "mod_authors" to libs.versions.mod.authors,
            "mod_description" to libs.versions.mod.description,
            "mod_id" to libs.versions.mod.id,
            "mod_license" to libs.versions.mod.license,
            "mod_name" to libs.versions.mod.name,
            "mod_version" to libs.versions.mod.version
        ).mapValues { it.value.get() }
        inputs.properties(propertiesMap)
        filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
            expand(buildMap {
                putAll(propertiesMap)
                put("project", project)
            })
        }
    }

    jar.configure {
        from("LICENSE") {
            rename { "${it}_${project.base.archivesName.get()}" }
        }

        manifest {
            attributes(
                hashMapOf(
                    "Specification-Title" to libs.versions.mod.id.get(),
                    "Specification-Vendor" to libs.versions.mod.authors.get(),
                    "Specification-Version" to "1",
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to archiveVersion.get(),
                    "Implementation-Vendor" to libs.versions.mod.authors.get(),
                    "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
                )
            )
        }
        finalizedBy("reobfJar")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}