description = "v1_13_R2"

repositories {
    mavenLocal()
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.13.2-R0.1-SNAPSHOT")
    compileOnly(project(":api"))
}
