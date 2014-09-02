package net.scottdukes.gradle.plugins

class SimpleAndroidExtension {
    private String target

    SimpleAndroidExtension() {
        setCompileSdkVersion 19
    }

    void compileSdkVersion(int apiLevel) {
        this.target = "android-" + apiLevel
    }

    void setCompileSdkVersion(int apiLevel) {
        compileSdkVersion(apiLevel)
    }

    public String getCompileSdkVersion() {
        return target
    }
}