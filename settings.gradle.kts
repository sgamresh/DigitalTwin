pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DigitalTwin"

include(
    ":app",
    ":core:common",
    ":core:model",
    ":core:database",
    ":domain:activity",
    ":data:activity",
    ":feature:tracker",
    ":feature:history",
    ":feature:edit",
)

