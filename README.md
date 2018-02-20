gradle-scalapb-plugin
==================

This plugin uses the [ScalaPB](http://scalapb.github.io) compiler to generate Scala case classes from protocol buffers
 and put them under the managed sources directory in your project.

You can configure the plugin using: 

```
scalapbConfig {
    dependentProtoSources = ["path/to/external/proto/sources", "or/some/other/path"]
    targetDir = "/target/scala/managed"
}
```