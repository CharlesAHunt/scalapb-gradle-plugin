package com.charlesahunt.scalapb

import org.scalatest.WordSpec
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
@RunWith(classOf[JUnitRunner])
class ScalaPBTest extends WordSpec {

  val testProjectDir = new TemporaryFolder

  "Protoc" should {

    "generate Scala classes from the given protos" in {
      val currentPath = new java.io.File(".").getCanonicalPath + "/src/test/"
      val files = ProtocPlugin.sourceGeneratorTask(
        projectRoot = currentPath, //getProject.getProjectDir.getAbsolutePath,
        projectProtoSourceDir = "resources/protos", //projectProtoSourceDir,
        protoIncludePaths = List(), // internalProtoSources ++ externalProtoSources ++ extractedIncludeDirs,
        gradleProtobufExtractedPrefix = "prefix",
        extractedIncludeDir = "protosCompiled", //pluginExtensions.extractedIncludeDir,
        targetDir = "protosTarget", //targetDir,
        grpc = true, //grpc
        protocVersion = "-v360",
        embeddedProtoc = false,
        javaConversions = false
      )

      assert(files.nonEmpty)
    }

    "generate scala classes from given protos when embedded is true" in {
      val currentPath = new java.io.File(".").getCanonicalPath + "/src/test/"
      val files = ProtocPlugin.sourceGeneratorTask(
        projectRoot = currentPath, //getProject.getProjectDir.getAbsolutePath,
        projectProtoSourceDir = "resources/protos", //projectProtoSourceDir,
        protoIncludePaths = List(), // internalProtoSources ++ externalProtoSources ++ extractedIncludeDirs,
        gradleProtobufExtractedPrefix = "prefix",
        extractedIncludeDir = "protosCompiled", //pluginExtensions.extractedIncludeDir,
        targetDir = "protosTarget", //targetDir,
        grpc = true, //grpc
        protocVersion = "-v360",
        embeddedProtoc = true,
        javaConversions = false
      )

      assert(files.nonEmpty)
    }

    "generate scala classes from given protos taking the flat package options into account" in {
      val currentPath = new java.io.File(".").getCanonicalPath + "/src/test/"
      val files = ProtocPlugin.sourceGeneratorTask(
        projectRoot = currentPath, //getProject.getProjectDir.getAbsolutePath,
        projectProtoSourceDir = "resources/protos", //projectProtoSourceDir,
        protoIncludePaths = List(), // internalProtoSources ++ externalProtoSources ++ extractedIncludeDirs,
        gradleProtobufExtractedPrefix = "prefix",
        extractedIncludeDir = "protosCompiled", //pluginExtensions.extractedIncludeDir,
        targetDir = "protosTarget", //targetDir,
        grpc = true, //grpc
        protocVersion = "-v360",
        embeddedProtoc = true,
        javaConversions = false,
        flatPackage = true
      )

      assert(files.nonEmpty)

      files.foreach { f =>
        assert(f.toString matches ".*/test/[A-z]+.scala")
      }

    }
  }
}
