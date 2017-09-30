package com.charlesahunt.scalapb;

import java.util.ArrayList;
import java.util.List;

public class ScalaPBPluginExtension {

    List<String> dependentProtoSources;

    List<String> getDependentProtoSources() {
        return dependentProtoSources;
    }

    void setDependentProtoSources(List<String> dependentProtoSources) {
        this.dependentProtoSources = dependentProtoSources;
    }

    public ScalaPBPluginExtension() {
        this.dependentProtoSources = new ArrayList<String>();
    }

}
