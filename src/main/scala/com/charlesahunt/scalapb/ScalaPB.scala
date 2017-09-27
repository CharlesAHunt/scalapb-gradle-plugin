package com.charlesahunt.scalapb

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ScalaPB extends DefaultTask {

  def getMessage(): String = { "test" }

  @TaskAction
  def sayGreeting(): Unit = {
    System.out.printf("IT WORKS!!")
  }

}