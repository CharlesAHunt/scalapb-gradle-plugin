gradle-scalapb-plugin
==================

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.charlesahunt/scalapb.gradle.plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.charlesahunt/scalapb.gradle.plugin)


This plugin uses the [ScalaPB](http://scalapb.github.io) compiler to generate Scala case classes from protocol buffers
 and put them under the managed sources directory in your project.

You can configure the plugin using: 

```
scalapbConfig {
    dependentProtoSources = ["path/to/external/proto/sources", "or/some/other/path"]
    targetDir = "/target/scala/managed"
    protocVersion = "-v360"
}
```