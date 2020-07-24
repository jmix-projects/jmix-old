# jmix

<p>
<a href="http://www.apache.org/licenses/LICENSE-2.0"><img src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat" alt="license" title=""></a>
<a href="https://travis-ci.org/jmix-framework/jmix"><img src="https://travis-ci.org/jmix-framework/jmix.svg?branch=master" alt="Build Status" title=""></a>
</p>

## Building

The root project is a composite build which comprises the Gradle plugin and the framework modules (see `includeBuild` directive in `settings.gradle`). So the following command will assemble and test both the plugin and the framework:

```
./gradlew build
```

## Publishing

In order to publish artifacts to the local Maven repo, execute the following commands:

```
./gradlew publishToMavenLocal install
./gradlew -b gradle-plugin/build.gradle publishToMavenLocal
./gradlew -b templates/studio/build.gradle publishToMavenLocal
```

> The `install` task is needed only to override `*.pom` files in a way that enables IJ IDEA to import library sources. See https://youtrack.jetbrains.com/issue/IDEA-227215.     

If you want to upload artefacts to a remote repository, set up the following properties in `~/.gradle/gradle.properties`:

```
jmixUploadUrl=<repo_url>
jmixUploadUser=<repo_user>
jmixUploadPassword=<repo_password>
```

If you are building a `*-SNAPSHOT` version, the build script will add `/snapshots` to `<repo_url>`, otherwise it will add `/releases`.