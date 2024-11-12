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
//        Url của MpChart
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "ChuyenDe"
include(":app")
