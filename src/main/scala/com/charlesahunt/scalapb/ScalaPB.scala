package com.charlesahunt.scalapb

import java.io.File

import com.typesafe.scalalogging.{LazyLogging, Logger}
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.{OutputDirectory, TaskAction}
import protocbridge.gens

class ScalaPB extends DefaultTask with LazyLogging {

  @TaskAction
  def compileProtos(): Unit = {
    val depPS = getProject.getExtensions.findByType(classOf[ScalaPBPluginExtension]).dependentProtoSources
    logger.info("Running scalapb compiler plugin for: " + getProject.getName)
    ProtocPlugin.sourceGeneratorTask(getProject.getProjectDir.getAbsolutePath)
  }

  @OutputDirectory
  def getOutputDir: File = new File(this.getPath+"/outputs")//TODO output dir should be set by config/args


}

import sbt.io._
import java.io.File
import protocbridge.Target

object ProtocPlugin extends LazyLogging {
  object autoImport {
    object PB {
//      val includePaths = SettingKey[Seq[File]]("protoc-include-paths", "The paths that contain *.proto dependencies.")
//      val externalIncludePath = SettingKey[File]("protoc-external-include-path", "The path to which protobuf:libraryDependencies are extracted and which is used as protobuf:includePath for protoc")
//      val generate = TaskKey[Seq[File]]("protoc-generate", "Compile the protobuf sources.")
//      val unpackDependencies = TaskKey[UnpackedDependencies]("protoc-unpack-dependencies", "Unpack dependencies.")
//      val protocOptions = SettingKey[Seq[String]]("protoc-options", "Additional options to be passed to protoc")
//      val protoSources = SettingKey[Seq[File]]("protoc-sources", "Directories to look for source files")
//      val targets = SettingKey[Seq[Target]]("protoc-targets", "List of targets to generate")
//
//      val runProtoc = SettingKey[Seq[String] => Int]("protoc-run-protoc", "A function that executes the protobuf compiler with the given arguments, returning the exit code of the compilation run.")
//      val protocVersion = SettingKey[String]("protoc-version", "Version flag to pass to protoc-jar")
//      val pythonExe =  SettingKey[String]("python-executable", "Full path for a Python.exe (needed only on Windows)")
//      val deleteTargetDirectory =  SettingKey[Boolean]("delete-target-directory", "Delete target directory before regenerating sources.")
//      val recompile = TaskKey[Boolean]("protoc-recompile")
//
//      val Target = protocbridge.Target
//      val gens = protocbridge.gens
    }
  }

  import autoImport.PB

//  val ProtobufConfig = config("protobuf")

//  override def projectConfigurations: Seq[Configuration] = Seq(ProtobufConfig)

//  def protobufGlobalSettings: Seq[Def.Setting[_]] = Seq(
//    includeFilter in PB.generate := "*.proto",
//    PB.externalIncludePath := target.value / "protobuf_external",
//
//    libraryDependencies ++= (PB.targets in Compile).value.flatMap(_.generator.suggestedDependencies.map(makeArtifact)),
//
//    managedClasspath in ProtobufConfig := {
//      val artifactTypes: Set[String] = (classpathTypes in ProtobufConfig).value
//      Classpaths.managedJars(ProtobufConfig, artifactTypes, (update in ProtobufConfig).value)
//    },
//    ivyConfigurations += ProtobufConfig,
//    ,
//    PB.pythonExe := "python",
//    PB.deleteTargetDirectory := true
//  )
//
//  // Settings that are applied at configuration (Compile, Test) scope.
//  def protobufConfigSettings: Seq[Setting[_]] = Seq(
//    arguments := Arguments(
//      includePaths = PB.includePaths.value,
//      protocOptions = PB.protocOptions.value,
//      pythonExe = PB.pythonExe.value,
//      deleteTargetDirectory = PB.deleteTargetDirectory.value,
//      targets = PB.targets.value.map(target => (target.outputPath, target.options))
//    ),
//    PB.recompile := {
//      import CacheArguments.instance
//      arguments.previous.exists(_ != arguments.value)
//    },
//    PB.protocOptions := Nil,
//    PB.protocOptions := PB.protocOptions.?.value.getOrElse(Nil),
//
//    PB.unpackDependencies := unpackDependenciesTask(PB.unpackDependencies).value,
//
//    PB.protoSources := PB.protoSources.?.value.getOrElse(Nil),
//    PB.protoSources += sourceDirectory.value / "protobuf",
//
//    PB.includePaths := PB.includePaths.?.value.getOrElse(Nil),
//    PB.includePaths ++= PB.protoSources.value,
//    PB.includePaths += PB.externalIncludePath.value,
//
//    PB.targets := PB.targets.?.value.getOrElse(Nil),
//
//    PB.generate := sourceGeneratorTask(PB.generate).dependsOn(PB.unpackDependencies).value,
//
//    PB.runProtoc := { args =>
//      com.github.os72.protocjar.Protoc.runProtoc(PB.protocVersion.value +: args.toArray)
//    },
//
//    sourceGenerators += PB.generate.taskValue
//  )

//  override def projectSettings: Seq[Def.Setting[_]] =
//    protobufGlobalSettings ++ inConfig(Compile)(protobufConfigSettings)

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
        pluginFrontend = protocbridge.frontend.PluginFrontend.newInstance(pythonExe=pythonExe))
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

  private[this] def unpack(deps: Seq[File], extractTarget: File, log: Logger): Seq[File] = {
    IO.createDirectory(extractTarget)
    deps.flatMap { dep =>
      val seq = IO.unzip(dep, extractTarget, "*.proto").toSeq
      if (seq.nonEmpty) log.debug("Extracted " + seq.mkString("\n * ", "\n * ", ""))
      seq
    }
  }

  def sourceGeneratorTask(path: String): Set[File] = {
//    val toInclude = (includeFilter in key).value
//    val toExclude = (excludeFilter in key).value
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
        includePaths = Seq(default), //PB.includePaths
        protocOptions = Seq(), //PB.protocOptions
        targets = Seq(Target(generator = gens.java, outputPath = new File(path+"/compiled_protobuf"), options = Seq.empty)), // PB.targets
        pythonExe = "python",
        false
      )
//    val cachedCompile = FileFunction.cached(
//      cacheFile, inStyle = FilesInfo.lastModified, outStyle = FilesInfo.exists) { (in: Set[File]) =>
//      compileProto()
//    }
//
//    if(PB.recompile.value) {
    logger.info("Running compileProto")
    compileProto()
//    } else {
//      cachedCompile(schemas).toSeq
//    }
  }

//  private[this] def unpackDependenciesTask(key: TaskKey[UnpackedDependencies]) = Def.task {
//    val extractedFiles = unpack((managedClasspath in (ProtobufConfig, key)).value.map(_.data), (PB.externalIncludePath in key).value, (streams in key).value.log)
//    UnpackedDependencies((PB.externalIncludePath in key).value, extractedFiles)
//  }
}