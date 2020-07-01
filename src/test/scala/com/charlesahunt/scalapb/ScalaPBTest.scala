package com.charlesahunt.scalapb

import org.scalatest.BeforeAndAfter
import org.scalatest.wordspec.AnyWordSpec

class ScalaPBTest extends AnyWordSpec with BeforeAndAfter {

  before {
    Seq("build/protosTarget", "build/protosCompiled").map(new java.io.File(_)).foreach {
      case dir if dir.exists() => dir.delete()
      case _ =>
    }
  }

  "Protoc" should {

    "generate Scala classes from the given protos" in {
      val currentPath = new java.io.File(".").getCanonicalPath
      val files = ProtocPlugin.sourceGeneratorTask(
        projectRoot = currentPath, //getProject.getProjectDir.getAbsolutePath,
        projectProtoSourceDir = "src/test/resources/protos", //projectProtoSourceDir,
        protoIncludePaths = List(), // internalProtoSources ++ externalProtoSources ++ extractedIncludeDirs,
        gradleProtobufExtractedPrefix = "prefix",
        extractedIncludeDir = "build/protosCompiled", //pluginExtensions.extractedIncludeDir,
        targetDir = "build/protosTarget", //targetDir,
        grpc = true, //grpc
        protocVersion = "-v360",
        embeddedProtoc = false,
        javaConversions = false
      )

      assert(files.nonEmpty)
    }

    "generate scala classes from given protos when embedded is true" in {
      val currentPath = new java.io.File(".").getCanonicalPath
      val files = ProtocPlugin.sourceGeneratorTask(
        projectRoot = currentPath, //getProject.getProjectDir.getAbsolutePath,
        projectProtoSourceDir = "src/test/resources/protos", //projectProtoSourceDir,
        protoIncludePaths = List(), // internalProtoSources ++ externalProtoSources ++ extractedIncludeDirs,
        gradleProtobufExtractedPrefix = "prefix",
        extractedIncludeDir = "build/protosCompiled", //pluginExtensions.extractedIncludeDir,
        targetDir = "build/protosTarget", //targetDir,
        grpc = true, //grpc
        protocVersion = "-v360",
        embeddedProtoc = true,
        javaConversions = false
      )

      assert(files.nonEmpty)
    }

    "generate scala classes from given protos taking the flat package options into account" in {
      val currentPath = new java.io.File(".").getCanonicalPath
      val files = ProtocPlugin.sourceGeneratorTask(
        projectRoot = currentPath, //getProject.getProjectDir.getAbsolutePath,
        projectProtoSourceDir = "src/test/resources/protos", //projectProtoSourceDir,
        protoIncludePaths = List(), // internalProtoSources ++ externalProtoSources ++ extractedIncludeDirs,
        gradleProtobufExtractedPrefix = "prefix",
        extractedIncludeDir = "build/protosCompiled", //pluginExtensions.extractedIncludeDir,
        targetDir = "build/protosTarget", //targetDir,
        grpc = true, //grpc
        protocVersion = "-v360",
        embeddedProtoc = true,
        javaConversions = false,
        flatPackage = true
      )

      assert(files.nonEmpty)

      files.foreach { f => assert(f.toString matches ".*/test/[A-z]+.scala") }

    }
  }
}
