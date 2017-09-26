package org.example.greeting;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

class ScalaPBPlugin extends Plugin[Project] {

    def apply(project: Project): Unit = {
        project.getTasks().create("hello", Greeting.class, (task) -> { 
            task.setMessage("Hello")
            task.setRecipient("World")
        })
    }

}