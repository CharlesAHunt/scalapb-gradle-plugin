package com.charlesahunt.scalapb

import org.gradle.api.Plugin
import org.gradle.api.Project

class ScalaPBPlugin extends Plugin[Project] {

    override def apply(project: Project): Unit = {
        project.getExtensions.create("scalapbConfig", classOf[ScalaPBPluginExtension])
        project.getTasks.create("scalapb", classOf[ScalaPB])
    }

}