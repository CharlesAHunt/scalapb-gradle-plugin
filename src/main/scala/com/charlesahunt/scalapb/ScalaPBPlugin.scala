package com.charlesahunt.scalapb

import org.gradle.api.Plugin
import org.gradle.api.Project

class ScalaPBPlugin extends Plugin[Project] {

    def apply(project: Project): Unit = {
        project.getTasks.create("hello", new ScalaPB().getClass)
    }

}