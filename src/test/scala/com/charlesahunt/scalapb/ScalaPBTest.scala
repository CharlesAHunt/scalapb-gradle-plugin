package com.charlesahunt.scalapb
import java.io.File

import org.scalatest.WordSpec
import protocbridge.Target

class ScalaPBTest extends WordSpec {

  "Protoc" should {
    "compile Scala code from the given protos" in {

      val protocVersion = "-v330"

      def protocCommand(arg: Seq[String]) = com.github.os72.protocjar.Protoc.runProtoc(protocVersion +: arg.toArray)

      val targets = List(Target.apply(scalapb.gen(),
        new File("src/test/scala/com/charlesahunt/scalapb")))

      val path = "src/test/scala/com/charlesahunt/scalapb/protos"
      val incPath = List(s"-I/Users/charleshunt/projects/scalapb-gradle-plugin/src/test/scala/com/charlesahunt/scalapb/protos", s"-I$path/protobuf_external/google/protobuf/descriptor.proto",
        s"-I$path/protobuf_external", s"-I$path/protobuf_external/google/protobuf/empty.proto", s"-I$path/protobuf_external/google/protobuf/type.proto", s"-I$path/protobuf_external/google/protobuf/timestamp.proto",
        s"-I$path/protobuf_external/google/protobuf/source_context.proto", s"-I$path/protobuf_external/google/protobuf/wrappers.proto",
        s"-I$path/protobuf_external/google/protobuf/field_mask.proto", s"-I$path/protobuf_external/google/protobuf/duration.proto",
        s"-I$path/protobuf_external/google/protobuf/struct.proto", s"-I$path/protobuf_external/google/protobuf/plugin.proto",
        s"-I$path/protobuf_external/google/protobuf/any.proto", s"-I$path/protobuf_external/google/protobuf/api.proto")
      val schemas = Set(new File(s"$path/Auth.proto"), new File(s"$path/Test.proto"))

      try {
        protocbridge.ProtocBridge.run(protocCommand, targets,
          incPath ++ schemas.map(_.getCanonicalPath),
          pluginFrontend = protocbridge.frontend.PluginFrontend.newInstance(pythonExe = "python")
        )
      } catch {
        case e: Exception =>
          throw new RuntimeException("error occurred while compiling protobuf files: %s" format (e.getMessage), e)
      }
    }
  }
}


