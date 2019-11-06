package com.charlesahunt.scalapb

import java.io.File
import java.nio.file.Paths

import com.github.os72.protocjar.ProtocVersion
import com.typesafe.scalalogging.LazyLogging
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.{InputFiles, OutputDirectory, TaskAction}
import sbt.io.{GlobFilter, PathFinder}

import scala.collection.JavaConverters._

class ScalaPB extends DefaultTask with LazyLogging {

  //Lazy so that the correct options are grabbed at runtime
  private lazy val pluginExtensions: ScalaPBPluginExtension = getProject.getExtensions
    .findByType(classOf[ScalaPBPluginExtension])

  private lazy val targetDir: String = pluginExtensions.targetDir

  @InputFiles
  def getSourceFiles: FileCollection = {
    val projectProtoSourceDir = pluginExtensions.projectProtoSourceDir
    val projectRoot = getProject.getProjectDir.getAbsolutePath
    val absoluteSourceDir = new File(s"$projectRoot/$projectProtoSourceDir")
    val schemas = ProtocPlugin.collectProtoSources(absoluteSourceDir)

    getProject.files(schemas.toList.asJava)
  }

  @TaskAction
  def compileProtos(): Unit = {
    // explicit list of includes
    val externalProtoSources = pluginExtensions.externalProtoSources.asScala.toList.map(new File(_))

    // I believe this will include scalapb-runtime
    val dependentProtoSources = getProject.getConfigurations
      .getByName("compile")
      .asScala
      .map(_.getAbsolutePath)
      .filter { p =>
        p.contains("protobuf") || p.contains("scalapb")
      }
      .map(new File(_))

    // we almost certainly want to compile protos in direct dependencies of this project
    // modulo whatever the caller wants to exclude / include
    val extractedIncludes = getProject.getTasksByName("extractIncludeProto", true).asScala.flatMap {
      task =>
        task.getOutputs.getFiles.getAsFileTree
          .matching(pluginExtensions.dependencySpec)
          .getFiles
          .asScala
    }

    logger.debug(s"picked up following includes from extractIncludeProto: $extractedIncludes")

    val protocVersion = pluginExtensions.protocVersion
    val javaConversions = pluginExtensions.javaConversions

    val projectProtoSourceDir = pluginExtensions.projectProtoSourceDir
    val grpc = pluginExtensions.grpc
    val embeddedProtoc = pluginExtensions.embeddedProtoc
    val resolvedGradleProtobufPathPrefix = Paths
      .get(getProject.getProjectDir.getAbsolutePath, pluginExtensions.gradleProtobufExtractedPrefix)
      .toString

    logger.info(s"using $resolvedGradleProtobufPathPrefix as gradle-protobuf-plugin prefix")

    logger.info("Running scalapb compiler plugin for: " + getProject.getName)
    ProtocPlugin.sourceGeneratorTask(
      projectRoot = getProject.getProjectDir.getAbsolutePath,
      projectProtoSourceDir = projectProtoSourceDir,
      protoIncludePaths = externalProtoSources ++ dependentProtoSources ++ extractedIncludes,
      gradleProtobufExtractedPrefix = resolvedGradleProtobufPathPrefix,
      extractedIncludeDir = pluginExtensions.extractedIncludeDir,
      targetDir = targetDir,
      grpc = grpc,
      protocVersion = protocVersion,
      embeddedProtoc = embeddedProtoc,
      javaConversions = javaConversions,
      flatPackage = pluginExtensions.flatPackage,
      singleLineToProtoString = pluginExtensions.singleLineToProtoString,
      asciiFormatToString = pluginExtensions.asciiFormatToString
    )
  }

  @OutputDirectory
  def getOutputDir: File = new File(s"${getProject.getProjectDir.getAbsolutePath}/$targetDir")
}

import java.io.File

import sbt.io._
import protocbridge.Target

object ProtocPlugin extends LazyLogging {

  case class UnpackedDependencies(dir: File, files: Seq[File])

  def collectProtoSources(absoluteSourceDir: File): Set[File] =
    List(absoluteSourceDir).toSet[File].flatMap { srcDir =>
      (PathFinder(srcDir) ** (GlobFilter("*.proto") /** -- toExclude**/ )).get
        .map(_.getAbsoluteFile)
    }

  private[this] def executeProtoc(
      protocCommand: Seq[String] => Int,
      schemas: Set[File],
      includePaths: Seq[File],
      protocOptions: Seq[String],
      targets: Seq[Target]
    ): Int =
    try {
      val incPath = includePaths.map("-I" + _.getCanonicalPath)
      protocbridge.ProtocBridge.run(
        protocCommand,
        targets,
        incPath ++ protocOptions ++ schemas.map(_.getCanonicalPath),
        pluginFrontend = protocbridge.frontend.PluginFrontend.newInstance
      )
    } catch {
      case error: Exception =>
        throw new RuntimeException(
          "Error occurred while compiling protobuf files: %s" format (error.getMessage),
          error
        )
    }

  private[this] def compile(
      protocCommand: Seq[String] => Int,
      schemas: Set[File],
      includePaths: Seq[File],
      protocOptions: Seq[String],
      targets: Seq[Target],
      deleteTargetDirectory: Boolean
    ): Set[File] = {
    // Sort by the length of path names to ensure that delete parent directories before deleting child directories.
    val generatedTargetDirs = targets.map(_.outputPath).sortBy(_.getAbsolutePath.length)
    generatedTargetDirs.foreach { targetDir =>
      if (deleteTargetDirectory)
        IO.delete(targetDir)

      targetDir.mkdirs()
    }

    if (schemas.nonEmpty && targets.nonEmpty) {
      logger.info(
        "Compiling %d protobuf files to %s".format(schemas.size, generatedTargetDirs.mkString(","))
      )
      protocOptions.map("\t" + _).foreach(logger.debug(_))
      schemas.foreach(schema => logger.info("Compiling schema %s" format schema))

      val exitCode = executeProtoc(protocCommand, schemas, includePaths, protocOptions, targets)
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

  def sourceGeneratorTask(
      projectRoot: String,
      projectProtoSourceDir: String,
      protoIncludePaths: List[File],
      gradleProtobufExtractedPrefix: String,
      extractedIncludeDir: String,
      targetDir: String,
      grpc: Boolean,
      protocVersion: String,
      embeddedProtoc: Boolean,
      javaConversions: Boolean,
      flatPackage: Boolean = false,
      singleLineToProtoString: Boolean = false,
      asciiFormatToString: Boolean = false
    ): Set[File] = {
    // protoc will use this as a staging dir
    val unpackProtosTo = new File(projectRoot, extractedIncludeDir)

    // unpack all protos from includes to staging area
    val unpackedProtos = unpack(protoIncludePaths, gradleProtobufExtractedPrefix, unpackProtosTo)
    logger.info("Unpacked Protos: " + unpackedProtos)

    val absoluteSourceDir = new File(s"$projectRoot/$projectProtoSourceDir")

    val schemas = collectProtoSources(absoluteSourceDir)
    val dependentSchemas = collectProtoSources(unpackProtosTo)

//    // Include Scala binary version like "_2.11" for cross building.
//    val cacheFile = (streams in key).value.cacheDirectory / s"protobuf_${scalaBinaryVersion.value}"
    def protocCommand(arg: Seq[String]) =
      com.github.os72.protocjar.Protoc.runProtoc(protocVersion +: arg.toArray)

    val target = Target(
      generatorAndOpts = scalapb.gen(
        grpc = grpc,
        javaConversions = javaConversions,
        flatPackage = flatPackage,
        singleLineToProtoString = singleLineToProtoString,
        asciiFormatToString = asciiFormatToString
      ),
      outputPath = new File(s"$projectRoot/$targetDir")
    )

    def compileProto(): Set[File] =
      compile(
        protocCommand = protocCommand,
        schemas = schemas ++ dependentSchemas,
        includePaths = Nil.+:(absoluteSourceDir).+:(unpackProtosTo),
        protocOptions =
          if (embeddedProtoc) Seq(s"-v${ProtocVersion.PROTOC_VERSION.mVersion}") else Nil,
        targets = Seq(target),
        deleteTargetDirectory = true
      )
//    val cachedCompile = FileFunction.cached(
//      cacheFile, inStyle = FilesInfo.lastModified, outStyle = FilesInfo.exists) { (in: Set[File]) =>
//      compileProto()
//    }

//    if(PB.recompile.value) {
    logger.info("Running compileProto")
    compileProto()
//    } else cachedCompile(schemas).toSeq
  }

  private[this] def unpack(
      deps: Seq[File],
      gradleProtobufExtractedPrefix: String,
      extractTarget: File
    ): Seq[File] = {
    logger.debug("Unpacking protos: " + deps.toString())
    IO.createDirectory(extractTarget)
    deps.flatMap { dep =>
      val seq = {
        val path = dep.getPath

        //TODO: maybe handle other kinds of dependency containers here
        if (path.endsWith(".zip") || path.endsWith(".gzip") || path.endsWith(".gz") || path
            .endsWith(".jar")) {
          IO.unzip(dep, extractTarget, "*.proto").toSeq
        } else if (path.endsWith(".proto")) {
          val target = if (path.startsWith(gradleProtobufExtractedPrefix)) {
            // this came from a gradleProtobuf output, try to maintain subdirectory structure
            Paths
              .get(extractTarget.getAbsolutePath, path.stripPrefix(gradleProtobufExtractedPrefix))
              .toFile
          } else {
            // otherwise just copy from wherever this came from
            Paths.get(extractTarget.getAbsolutePath, dep.getName).toFile
          }
          IO.copy(Seq((dep, target)))
        } else if (dep.isDirectory) {
          IO.copyDirectory(dep, extractTarget, overwrite = true, preserveLastModified = true)

          // recalculate destination files, just like copyDirectory
          PathFinder(dep).allPaths.get.flatMap { file =>
            Path.rebase(dep, extractTarget)(file)
          }
        } else {
          logger.warn(s"Unknown dependency type: $dep")
          Nil
        }
      }

      logger.debug("Extracted " + seq.mkString("\n * ", "\n * ", ""))
      seq
    }
  }
}
