object Config {
    val minSdk = 16
    val compileSdk = 28
    val targetSdk = 28
    val buildTools = "29.0.3"
}

object Versions {
    const val kotlin = "1.4.32"
}

object Deps {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    const val koin = "org.koin:koin-android:1.0.2"
    const val koinVm = "org.koin:koin-android-viewmodel:1.0.2"

    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.7"

    const val room = "androidx.room:room-runtime:2.2.5"
    const val roomCompiler = "androidx.room:room-compiler:2.2.5"

    const val gson = "com.google.code.gson:gson:2.8.6"

    const val appcompat = "androidx.appcompat:appcompat:1.2.0"

    const val junit = "junit:junit:4.13.2"
}