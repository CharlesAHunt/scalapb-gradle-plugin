package com.charlesahunt.scalapb;

import java.util.ArrayList;
import java.util.List;

public class ScalaPBPluginExtension {

    List<String> dependentProtoSources;
    String targetDir;
    String projectProtoSourceDir;

    List<String> getDependentProtoSources() {
        return dependentProtoSources;
    }

    String getTargetDir() {
        return targetDir;
    }

    String getProjectProtoSourceDir() {
        return projectProtoSourceDir;
    }

    void setDependentProtoSources(List<String> dependentProtoSources) {
        this.dependentProtoSources = dependentProtoSources;
    }

    void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    void setProjectProtoSourceDir(String projectProtoSourceDir) {
        this.projectProtoSourceDir = projectProtoSourceDir;
    }

    public ScalaPBPluginExtension() {
        this.dependentProtoSources = new ArrayList<String>();
        this.targetDir = "";
        this.projectProtoSourceDir = "src/main/protobuf";
    }



}
