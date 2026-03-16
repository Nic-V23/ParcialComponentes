// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
<<<<<<< HEAD
    id("com.google.devtools.ksp") version "2.1.10-1.0.31" apply false
    alias(libs.plugins.kotlin.android) apply false  // ← cambiar
}
=======
    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
}
>>>>>>> f3fb4cab155fc790946618dedbe00a137d18d5ce
