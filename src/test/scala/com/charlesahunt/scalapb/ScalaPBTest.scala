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
        currentPath, //getProject.getProjectDir.getAbsolutePath,
        "resources/protos", //projectProtoSourceDir,
        List(),// internalProtoSources ++ externalProtoSources ++ extractedIncludeDirs,
        "prefix",
        "protosCompiled", //pluginExtensions.extractedIncludeDir,
        "protosTarget", //targetDir,
        true,//grpc
        "-v361",
        false,
        false
      )

      assert(files.nonEmpty)
    }

    "generate scala classes from given protos when embedded is true" in {
      val currentPath = new java.io.File(".").getCanonicalPath + "/src/test/"
      val files = ProtocPlugin.sourceGeneratorTask(
        currentPath, //getProject.getProjectDir.getAbsolutePath,
        "resources/protos", //projectProtoSourceDir,
        List(),// internalProtoSources ++ externalProtoSources ++ extractedIncludeDirs,
        "prefix",
        "protosCompiled", //pluginExtensions.extractedIncludeDir,
        "protosTarget", //targetDir,
        true,//grpc
        "-v361",
        true,
        false
      )

      assert(files.nonEmpty)
    }
  }
}
