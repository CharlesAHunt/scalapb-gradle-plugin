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
        "protosCompiled", //pluginExtensions.extractedIncludeDir,
        "protosTarget", //targetDir,
        true,//grpc
        "-v360",
        true
      )

      assert(files.nonEmpty)
    }

    "..." in {
      val currentPath = new java.io.File(".").getCanonicalPath + "/src/test/"
      val files = ProtocPlugin.sourceGeneratorTask(
        currentPath, //getProject.getProjectDir.getAbsolutePath,
        "resources/protos", //projectProtoSourceDir,
        List(),// internalProtoSources ++ externalProtoSources ++ extractedIncludeDirs,
        "protosCompiled", //pluginExtensions.extractedIncludeDir,
        "protosTarget", //targetDir,
        true,//grpc
        "-v360",
        false
      )

      assert(files.nonEmpty)
    }
  }
}
