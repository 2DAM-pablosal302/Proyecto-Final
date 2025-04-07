pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/spotify/android-sdk")
            credentials {
                username = "2DAM-pablosal302" // Puedes usar cualquier nombre
                password = "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" // Necesitas un token personal de GitHub
            }
        }
    }
}

rootProject.name = "StelarSound"
include(":app")
