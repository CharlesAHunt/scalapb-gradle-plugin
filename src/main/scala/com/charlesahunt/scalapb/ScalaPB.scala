package com.charlesahunt.scalapb

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.{OutputDirectory, TaskAction}
import scala.collection.JavaConverters._

class ScalaPB extends DefaultTask with LazyLogging {

  @TaskAction
  def compileProtos(): Unit = {
    val internalProtoSources = getProject.getExtensions.findByType(classOf[ScalaPBPluginExtension]).dependentProtoSources.asScala.toList.map(new File(_))
    val externalProtoSources = getProject.getConfigurations.getByName("compile").asScala.toList.filter(_.getAbsolutePath.contains("protobuf"))
    logger.info("Running scalapb compiler plugin for: " + getProject.getName)
    ProtocPlugin.sourceGeneratorTask(getProject.getProjectDir.getAbsolutePath, internalProtoSources ++ externalProtoSources)
  }

  @OutputDirectory
  def getOutputDir: File = new File(this.getPath+"/outputs")//TODO output dir should be set by config/args


}

import sbt.io._
import java.io.File
import protocbridge.Target

object ProtocPlugin extends LazyLogging {

  case class UnpackedDependencies(dir: File, files: Seq[File])

  private[this] def executeProtoc(
    protocCommand: Seq[String] => Int,
    schemas: Set[File],
    includePaths: Seq[File],
    protocOptions: Seq[String],
    targets: Seq[Target],
    pythonExe: String
  ) : Int =
    try {
      val incPath = includePaths.map("-I" + _.getCanonicalPath)
      protocbridge.ProtocBridge.run(protocCommand, targets,
        incPath ++ protocOptions ++ schemas.map(_.getCanonicalPath),
          pluginFrontend = protocbridge.frontend.PluginFrontend.newInstance(pythonExe=pythonExe)
      )
    } catch { case e: Exception =>
      throw new RuntimeException("error occurred while compiling protobuf files: %s" format(e.getMessage), e)
  }

  private[this] def compile(
    protocCommand: Seq[String] => Int,
    schemas: Set[File],
    includePaths: Seq[File],
    protocOptions: Seq[String],
    targets: Seq[Target],
    pythonExe: String,
    deleteTargetDirectory: Boolean,
  ): Set[File] = {
    // Sort by the length of path names to ensure that delete parent directories before deleting child directories.
    val generatedTargetDirs = targets.map(_.outputPath).sortBy(_.getAbsolutePath.length)
    generatedTargetDirs.foreach{ targetDir =>
      if (deleteTargetDirectory)
        IO.delete(targetDir)

      targetDir.mkdirs()
    }

    if (schemas.nonEmpty && targets.nonEmpty) {
      logger.info("Compiling %d protobuf files to %s".format(schemas.size, generatedTargetDirs.mkString(",")))
      protocOptions.map("\t"+_).foreach(logger.debug(_))
      schemas.foreach(schema => logger.info("Compiling schema %s" format schema))

      val exitCode = executeProtoc(protocCommand, schemas, includePaths, protocOptions, targets, pythonExe)
      if (exitCode != 0)
        sys.error("protoc returned exit code: %d" format exitCode)

      logger.info("Compiling protobuf")
      generatedTargetDirs.foreach { dir =>
        logger.info("Protoc target directory: %s".format(dir.getAbsolutePath))
      }

      targets.flatMap { ot =>
        (PathFinder(ot.outputPath) ** (GlobFilter("*.java") | GlobFilter("*.scala"))).get
      }.toSet
    } else if (schemas.nonEmpty && targets.isEmpty) {
      logger.info("Protobufs files found, but PB.targets is empty.")
      Set[File]()
    } else {
      logger.info("PB.targets is found but Protobufs files are empty")
      Set[File]()
    }
  }

  def sourceGeneratorTask(path: String, protocPaths: List[File]): Set[File] = {
    val unpackProtosTo = new File(path+"/target/protobuf_external")
    val unpackedProtos = unpack(protocPaths, unpackProtosTo)
    logger.info("unpacked Protos:  "+unpackedProtos)
    val default = new File(path+"/src/main/protobuf")
    val schemas = List(default).toSet[File].flatMap { srcDir =>
      (PathFinder(srcDir) ** (GlobFilter("*.proto") /** -- toExclude**/)).get.map(_.getAbsoluteFile)
    }
//    // Include Scala binary version like "_2.11" for cross building.
//    val cacheFile = (streams in key).value.cacheDirectory / s"protobuf_${scalaBinaryVersion.value}"
    val protocVersion = "-v330"
    def protocCommand(arg: Seq[String]) = com.github.os72.protocjar.Protoc.runProtoc(protocVersion +: arg.toArray)
    def compileProto(): Set[File] =
      compile(
        protocCommand = protocCommand,
        schemas = schemas,
        includePaths = Nil.+:(default).+:(unpackProtosTo),
        protocOptions = Nil,
        targets = Seq(Target(generatorAndOpts = scalapb.gen(), outputPath = new File(path+"/compiled_protobuf"))),
        pythonExe = "python",
        deleteTargetDirectory = true
      )
//    val cachedCompile = FileFunction.cached(
//      cacheFile, inStyle = FilesInfo.lastModified, outStyle = FilesInfo.exists) { (in: Set[File]) =>
//      compileProto()
//    }

//    if(PB.recompile.value) {
    logger.info("Running compileProto")
    compileProto()
//    } else {
//      cachedCompile(schemas).toSeq
//    }
  }

  private[this] def unpack(deps: Seq[File], extractTarget: File): Seq[File] = {
    IO.createDirectory(extractTarget)
    deps.flatMap { dep =>
      val seq = IO.unzip(dep, extractTarget, "*.proto").toSeq
      if (seq.nonEmpty) logger.debug("Extracted " + seq.mkString("\n * ", "\n * ", ""))
      seq
    }
  }
}