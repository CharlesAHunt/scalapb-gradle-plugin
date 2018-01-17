package com.charlesahunt.scalapb

import java.io.File
import java.nio.file.Paths

import com.typesafe.scalalogging.LazyLogging
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.{OutputDirectory, TaskAction}
import sbt.io.{GlobFilter, PathFinder}

import scala.collection.JavaConverters._

class ScalaPB extends DefaultTask with LazyLogging {

  private val configuredTargetDir: Option[String] = Option(getProject.getExtensions.findByType(classOf[ScalaPBPluginExtension]).targetDir)
  val targetDir: String = (
      if(configuredTargetDir.isEmpty || configuredTargetDir.getOrElse("").isEmpty) None else configuredTargetDir
    ).getOrElse("target/scala")

  @TaskAction
  def compileProtos(): Unit = {
    val pluginExtensions = getProject.getExtensions.findByType(classOf[ScalaPBPluginExtension])

    // explicit list of includes
    val internalProtoSources = pluginExtensions.dependentProtoSources.asScala.toList.map(new File(_))

    // potentially proto-containing jars in compile dep path
    val externalProtoSources = getProject.getConfigurations.getByName("compile").asScala.filter( c =>
      c.getAbsolutePath.contains("protobuf") || c.getAbsolutePath.contains("scalapb")
    )

    // .protos from google's gradle-protobuf plugin after extraction
    val gradleProtobufSources = getProject.getTasksByName("extractIncludeProto", true).asScala
        .flatMap(_.getOutputs.getFiles.asScala.map(_.getAbsoluteFile))

    val gradlePluginProtos = (PathFinder(gradleProtobufSources) ** GlobFilter("*.proto"))
        .get.map(_.getAbsoluteFile)

    // project's proto sources
    val projectProtoSourceDir = pluginExtensions.projectProtoSourceDir

    logger.info("Running scalapb compiler plugin for: " + getProject.getName)
    ProtocPlugin.sourceGeneratorTask(
      getProject.getProjectDir.getAbsolutePath,
      projectProtoSourceDir,
      internalProtoSources ++ externalProtoSources ++ gradlePluginProtos,
      targetDir)
  }

  @OutputDirectory
  def getOutputDir: File = new File(s"${getProject.getProjectDir.getAbsolutePath}/$targetDir")

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
    deleteTargetDirectory: Boolean
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
      logger.info("Protobuf files(schemas) found, but targets is empty.")
      Set[File]()
    } else {
      logger.info("targets is found but Protobuf files(schemas) are empty")
      Set[File]()
    }
  }

  def sourceGeneratorTask(projectRoot: String,
                          projectProtoSourceDir: String,
                          protoIncludePaths: List[File],
                          targetDir: String): Set[File] = {
    val unpackProtosTo = new File(projectRoot+"/target/protobuf_external")
    val unpackedProtos = unpack(protoIncludePaths, unpackProtosTo)
    logger.info("unpacked Protos:  "+unpackedProtos)
    val default = new File(s"$projectRoot/$projectProtoSourceDir")

    val schemas = List(default).toSet[File].flatMap { srcDir =>
      (PathFinder(srcDir) ** (GlobFilter("*.proto") /** -- toExclude**/)).get.map(_.getAbsoluteFile)
    }
//    // Include Scala binary version like "_2.11" for cross building.
//    val cacheFile = (streams in key).value.cacheDirectory / s"protobuf_${scalaBinaryVersion.value}"
    val protocVersion = "-v340"
    def protocCommand(arg: Seq[String]) = com.github.os72.protocjar.Protoc.runProtoc(protocVersion +: arg.toArray)
    def compileProto(): Set[File] =
      compile(
        protocCommand = protocCommand,
        schemas = schemas,
        includePaths = Nil.+:(default).+:(unpackProtosTo),
        protocOptions = Nil,
        targets = Seq(Target(generatorAndOpts = scalapb.gen(), outputPath = new File(s"$projectRoot/$targetDir"))),
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
      val seq = {
        val path = dep.getPath

        // todo maybe handle other kinds of dependency containers here
        if (path.endsWith(".zip") || path.endsWith(".gzip") || path.endsWith(".gz")) {
          IO.unzip(dep, extractTarget, "*.proto").toSeq
        }
        else if (path.endsWith(".proto")) {
          val targetName = Paths.get(extractTarget.getAbsolutePath, dep.getName).toFile
          IO.copy(Seq((dep, targetName)))
        }
        else {
          // not sure what kind of dependency this was
          Nil
        }
      }

      if (seq.nonEmpty) logger.debug("Extracted " + seq.mkString("\n * ", "\n * ", ""))
      seq
    }
  }
}