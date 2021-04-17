import org.gradle.api.Action
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.maven.MavenPomDeveloperSpec
import org.gradle.api.publish.maven.MavenPomLicenseSpec
import org.gradle.api.publish.maven.MavenPomScm
import org.gradle.api.publish.maven.MavenPublication
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.util.Properties

object Publishing {

    private val localPropsFile = File("local.properties")

    private val localProps: Properties by lazy {
        Properties().apply {
            load(FileInputStream(localPropsFile))
        }
    }

    val signingKeyId: String by lazy {
        localProps["signing.keyId"]?.toString() ?: System.getenv("SIGNING_KEY_ID")
    }
    val signingPassword: String by lazy {
        localProps["signing.password"]?.toString() ?: System.getenv("SIGNING_PASSWORD")
    }
    val signingSecretKeyring: String by lazy {
        localProps["signing.secretKeyRingFile"]?.toString() ?: System.getenv("SIGNING_SECRET_KEY_RING_FILE")
    }
//    val ossrhUsername: String
//    val ossrhPassword: String
//    val sonatypeStagingProfileId: String
//
//    init {
//        localProps.load(FileInputStream(File("local.properties")))
//
//        ossrhUsername = localProps["ossrhUsername"]?.toString() ?: System.getenv("OSSRH_USERNAME")
//        ossrhPassword = localProps["ossrhPassword"]?.toString() ?: System.getenv("OSSRH_PASSWORD")
//        sonatypeStagingProfileId = localProps["sonatypeStagingProfileId"]?.toString() ?: System.getenv("SONATYPE_STAGING_PROFILE_ID")
//    }

    val scm: Action<MavenPomScm> = Action {
        connection.set("scm:git:github.com/boswelja/WatchConnectionLib.git")
        developerConnection.set("scm:git:ssh://github.com/boswelja/WatchConnectionLib.git")
        url.set("https://github.com/boswelja/WatchConnectionLib")
    }

    val licenses: Action<MavenPomLicenseSpec> = Action {
        license {
            name.set("Apache 2.0")
            url.set("https://github.com/boswelja/WatchConnectionLib/blob/main/LICENSE")
        }
    }

    val developers: Action<MavenPomDeveloperSpec> = Action {
        developer {
            id.set("boswelja")
            name.set("Jack Boswell")
            email.set("boswelja@outlook.com")
            url.set("https://boswelja.github.io")
        }
    }

    val repositories: Action<RepositoryHandler> = Action {
        maven {
            name = "sonatype"
            url = URI("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = localProps["ossrhUsername"]?.toString() ?: System.getenv("OSSRH_USERNAME")
                password = localProps["ossrhPassword"]?.toString() ?: System.getenv("OSSRH_PASSWORD")
            }
        }
    }

    fun PublicationContainer.configureMavenPublication(
        artifactId: String,
        description: String,
        url: String,
        dependencySet: DependencySet,
        configuration: MavenPublication.() -> Unit
    ): MavenPublication.() -> Unit {
        return {
            configuration()
            groupId = "io.github.boswelja.watchconnection"
            this.artifactId = artifactId
            version = "0.1.0" // TODO Plug version in

            pom {
                name.set(artifactId)
                this.description.set(description)
                this.url.set(url)

                licenses(licenses)
                developers(developers)
                scm(scm)

                withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")

                    dependencySet.forEach {
                        val dependencyNode = dependenciesNode.appendNode("dependency")
                        dependencyNode.appendNode("groupId", it.group)
                        dependencyNode.appendNode("artifactId", it.name)
                        dependencyNode.appendNode("version", it.version)
                    }
                }
            }
        }
    }
}