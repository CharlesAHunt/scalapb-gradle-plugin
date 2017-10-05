package com.charlesahunt.scalapb;

import java.util.ArrayList;
import java.util.List;

public class ScalaPBPluginExtension {

    List<String> dependentProtoSources;
    String targetDir;

    List<String> getDependentProtoSources() {
        return dependentProtoSources;
    }

    String getTargetDir() {
        return targetDir;
    }

    void setDependentProtoSources(List<String> dependentProtoSources) {
        this.dependentProtoSources = dependentProtoSources;
    }

    void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    public ScalaPBPluginExtension() {
        this.dependentProtoSources = new ArrayList<String>();
        this.targetDir = "";
    }

}
