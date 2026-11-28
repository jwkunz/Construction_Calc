plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// One version for the whole project: the VERSION file at the repository root.
val projectVersion = rootProject.file("../VERSION").readText().trim()
val versionParts = projectVersion.split(".").map { it.toInt() }
val androidVersionCode = versionParts[0] * 10_000 + versionParts[1] * 100 + versionParts[2]

android {
    namespace = "com.jwkunz.constructioncalc"
    compileSdk = 36

    sourceSets {
        getByName("main") {
            assets.srcDir("build/generated/web-assets")
        }
    }

    defaultConfig {
        applicationId = "com.jwkunz.constructioncalc"
        minSdk = 24
        targetSdk = 36
        versionCode = androidVersionCode
        versionName = projectVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Release signing comes from the environment (GitHub Actions secrets).
    // Without it, release builds are left unsigned.
    val releaseStoreFile = System.getenv("RELEASE_STORE_FILE")
    signingConfigs {
        if (!releaseStoreFile.isNullOrEmpty()) {
            create("release") {
                storeFile = file(releaseStoreFile)
                storePassword = System.getenv("RELEASE_STORE_PASSWORD")
                keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

// Builds the web app (../web) into one self-contained HTML file and bundles it as the app's index.html.
// Skip with -PskipWebBuild to reuse an existing build in ../web.
val buildWeb by tasks.registering(Exec::class) {
    workingDir = rootProject.file("../web")
    commandLine("./scripts/single_file_package.sh")
    onlyIf { !project.hasProperty("skipWebBuild") }
}

val copyWeb by tasks.registering(Copy::class) {
    dependsOn(buildWeb)
    from(fileTree(rootProject.file("../web")) { include("construction_calc_v*.html") }) { rename { "index.html" } }
    into(layout.buildDirectory.dir("generated/web-assets"))
}

tasks.named("preBuild") { dependsOn(copyWeb) }

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.webkit:webkit:1.13.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
