plugins {
    id("dev.isxander.modstitch.base") version "0.5.14-unstable"
    id("dev.kikugie.j52j") version "2.0"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}

fun prop(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)
        ?.let(consumer)
}

val baseVersion = property("mod.version") as String
val minecraft = property("deps.minecraft") as String;
val loader = when {
    modstitch.isLoom -> "fabric"
    modstitch.isModDevGradleRegular -> "neoforge"
    modstitch.isModDevGradleLegacy -> "forge"
    else -> throw IllegalStateException("Unsupported Loader")
}

j52j {
    params {
        prettyPrinting = true
    }
}

modstitch {
    minecraftVersion = minecraft

    // Alternatively use stonecutter.eval if you have a lot of versions to target.
    // https://stonecutter.kikugie.dev/stonecutter/guide/setup#checking-versions
    var jvm1206 = 17
    if (loader == "neoforge") {
        jvm1206 = 21
    }
    javaTarget = when (minecraft) {
        "1.20.1" -> 17
        "1.20.4" -> 17
        "1.20.6" -> jvm1206
        "1.21.1" -> 21
        else -> throw IllegalArgumentException("Please store the java version for ${property("deps.minecraft")} in build.gradle.kts!")
    }

    // If parchment doesnt exist for a version yet you can safely
    // omit the "deps.parchment" property from your versioned gradle.properties
    parchment {
        prop("deps.parchment") { mappingsVersion = it }
    }

    // This metadata is used to fill out the information inside
    // the metadata files found in the templates folder.
    metadata {
        modId = "opacwelcomesuppressor"
        modName = "OPAC Welcome Suppressor"
        modVersion = "${baseVersion}+${minecraft}-${loader}"
        modGroup = "dev.spagurder"
        modAuthor = "murder_spagurder"
        modDescription = "Suppress OPAC Claim Welcome Messages"

        fun <K, V> MapProperty<K, V>.populate(block: MapProperty<K, V>.() -> Unit) {
            block()
        }

        replacementProperties.populate {
            // You can put any other replacement properties/metadata here that
            // modstitch doesn't initially support. Some examples below.
            put("mod_issue_tracker", "https://github.com/murderspagurder/opac-welcome-suppressor/issues")
            put("pack_format", when (property("deps.minecraft")) {
                "1.20.1" -> 15
                "1.20.4" -> 22
                "1.20.6" -> 32
                "1.21.1" -> 34
                else -> throw IllegalArgumentException("Please store the resource pack version for ${property("deps.minecraft")} in build.gradle.kts! https://minecraft.wiki/w/Pack_format")
            }.toString())
        }
    }

    // Fabric Loom (Fabric)
    loom {
        // It's not recommended to store the Fabric Loader version in properties.
        // Make sure its up to date.
        fabricLoaderVersion = "0.16.10"

        // Configure loom like normal in this block.
        configureLoom {

        }
    }

    // ModDevGradle (NeoForge, Forge, Forgelike)
    moddevgradle {
        enable {
            prop("deps.forge") { forgeVersion = it }
            prop("deps.neoform") { neoFormVersion = it }
            prop("deps.neoforge") { neoForgeVersion = it }
            prop("deps.mcp") { mcpVersion = it }
        }

        // Configures client and server runs for MDG, it is not done by default
        defaultRuns()

        // This block configures the `neoforge` extension that MDG exposes by default,
        // you can configure MDG like normal from here
        configureNeoforge {
            runs.all {
                disableIdeRun()
            }
        }
    }

    mixin {
        // You do not need to specify mixins in any mods.json/toml file if this is set to
        // true, it will automatically be generated.
        addMixinsToModManifest = true

        configs.register("opacwelcomesuppressor")

        // Most of the time you wont ever need loader specific mixins.
        // If you do, simply make the mixin file and add it like so for the respective loader:
        // if (isLoom) configs.register("examplemod-fabric")
        // if (isModDevGradleRegular) configs.register("examplemod-neoforge")
        // if (isModDevGradleLegacy) configs.register("examplemod-forge")
    }
}

// Stonecutter constants for mod loaders.
// See https://stonecutter.kikugie.dev/stonecutter/guide/comments#condition-constants
var constraint: String = name.split("-")[1]
stonecutter {
    consts(
        "fabric" to constraint.equals("fabric"),
        "neoforge" to constraint.equals("neoforge"),
        "forge" to constraint.equals("forge"),
        "vanilla" to constraint.equals("vanilla")
    )
}

// All dependencies should be specified through modstitch's proxy configuration.
// Wondering where the "repositories" block is? Go to "stonecutter.gradle.kts"
// If you want to create proxy configurations for more source sets, such as client source sets,
// use the modstitch.createProxyConfigurations(sourceSets["client"]) function.
dependencies {
    modstitch.loom {
        modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:0.112.0+1.21.4")
    }

    // Anything else in the dependencies block will be used for all platforms.
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        outputs.upToDateWhen { false } // work around modstitch mixin cache issue
    }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(modstitch.finalJarTask.map { it.archiveFile })
    into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    dependsOn("build")
}

val additionalVersionsStr = findProperty("publish.additionalVersions") as String?
val additionalVersions: List<String> = additionalVersionsStr
    ?.split(",")
    ?.map { it.trim() }
    ?.filter { it.isNotEmpty() }
    ?: emptyList()
publishMods {
    version = "${baseVersion}+${minecraft}-${loader}"
    displayName = "OPAC Welcome Suppressor $baseVersion for $loader $minecraft"
    file = modstitch.finalJarTask.flatMap { it.archiveFile }
    type = STABLE
    modLoaders.add(loader)
    changelog = rootProject.file("CHANGELOG.md").readText()

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_API_KEY")
        projectId = "SnVkOjm4"
        minecraftVersions.add(minecraft)
        minecraftVersions.addAll(additionalVersions)
    }

    curseforge {
        accessToken = providers.environmentVariable("CURSEFORGE_API_KEY")
        projectId = "1297349"
        minecraftVersions.add(minecraft)
        minecraftVersions.addAll(additionalVersions)
    }
}
