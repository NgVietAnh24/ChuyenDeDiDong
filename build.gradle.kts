// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.6.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

// Nếu cần thêm bất kỳ dependency nào cho Google Services trong dự án,
// bạn có thể kích hoạt lại phần `buildscript` dưới đây, nhưng cần chú ý không bị trùng lặp.
buildscript {
    dependencies {
        // Sử dụng phiên bản mới nhất của Google Services nếu cần thiết
        classpath("com.google.gms:google-services:4.3.15")
    }
}
