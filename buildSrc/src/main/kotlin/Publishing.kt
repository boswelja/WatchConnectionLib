import org.gradle.api.Action
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.dsl.RepositoryHandler
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
            try {
                load(FileInputStream(localPropsFile))
            } catch (e: Exception) { }
        }
    }

    val version: String by lazy {
        localProps["version"]?.toString() ?: System.getenv("VERSION")
    }

    val ossrhUsername: String by lazy {
        localProps["ossrhUsername"]?.toString() ?: System.getenv("OSSRH_USERNAME")
    }
    val ossrhPassword: String by lazy {
        localProps["ossrhPassword"]?.toString() ?: System.getenv("OSSRH_PASSWORD")
    }

    val groupId = "io.github.boswelja.watchconnection"

    val signingKeyId: String by lazy {
        localProps["signing.keyId"]?.toString() ?: System.getenv("SIGNING_KEY_ID")
    }
    val signingPassword: String by lazy {
        localProps["signing.password"]?.toString() ?: System.getenv("SIGNING_PASSWORD")
    }
    val signingSecretKeyring: String by lazy {
        localProps["signing.secretKeyRingFile"]?.toString() ?: System.getenv("SIGNING_SECRET_KEY_RING_FILE")
    }

    val stagingProfileId: String by lazy {
        localProps["sonatypeStagingProfileId"]?.toString() ?: System.getenv("SONATYPE_STAGING_PROFILE_ID")
    }

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
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }

    fun configureMavenPublication(
        artifactId: String,
        description: String,
        url: String,
        dependencySet: DependencySet,
        configuration: MavenPublication.() -> Unit
    ): MavenPublication.() -> Unit = {
        configuration()
        this.groupId = this@Publishing.groupId
        this.artifactId = artifactId
        version = version

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
