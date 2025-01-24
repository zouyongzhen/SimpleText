plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.mavenPublish)
}

android {
    namespace = "zyz.hero.simple_text"
    compileSdk = 34

    defaultConfig {
        minSdk = 16
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.material)
}
group = "com.github.zouyongzhen"
version = "1.0-alpha"
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = (group.toString())
                artifactId = "SimpleText"
                version = version
            }
        }
    }
}
