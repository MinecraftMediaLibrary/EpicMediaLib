description = "v1_8_R2"

repositories {
    mavenLocal()
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.8.3-R0.1-SNAPSHOT")
    compileOnly(project(":api"))
}
