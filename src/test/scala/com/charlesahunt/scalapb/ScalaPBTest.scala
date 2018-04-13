package com.charlesahunt.scalapb

import java.io.File

import org.scalatest.WordSpec
import protocbridge.Target
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.scalatest.WordSpec
import java.io._

import org.gradle.api.internal.GradleInternal
import org.gradle.api.internal.initialization.ClassLoaderScope
import org.gradle.api.internal.project.{DefaultProject, ProjectInternal}
import org.gradle.groovy.scripts.ScriptSource
import org.gradle.internal.service.scopes.ServiceRegistryFactory
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.TaskOutcome._
import org.junit.rules.TemporaryFolder

//TODO
class ScalaPBTest extends WordSpec {

  val testProjectDir = new TemporaryFolder

  "Protoc" should {
    "compile Scala code from the given protos" in {

      val result = GradleRunner.create
        .withProjectDir(testProjectDir.getRoot)
        .withArguments("scalapb")
        .withPluginClasspath()
        .build

      val taskResult = result.task(":scalapb")

    }
  }
}
