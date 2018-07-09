package com.charlesahunt.scalapb;

import java.util.ArrayList;
import java.util.List;

public class ScalaPBPluginExtension {

    List<String> dependentProtoSources;
    String protocVersion;
    String targetDir;
    String projectProtoSourceDir;
    String extractedIncludeDir;
    Boolean grpc;

    List<String> getDependentProtoSources() {
        return dependentProtoSources;
    }

    String getProtocVersion() {
        return protocVersion;
    }

    String getTargetDir() {
        return targetDir;
    }

    String getProjectProtoSourceDir() {
        return projectProtoSourceDir;
    }

    String getExtractedIncludeDir() {
        return extractedIncludeDir;
    }

    Boolean getGrpc() { return grpc; }

    void setDependentProtoSources(List<String> dependentProtoSources) {
        this.dependentProtoSources = dependentProtoSources;
    }

    void setProtocVersion(String protocVersion) {
        this.protocVersion = protocVersion;
    }

    void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    void setProjectProtoSourceDir(String projectProtoSourceDir) {
        this.projectProtoSourceDir = projectProtoSourceDir;
    }

    void setExtractedIncludeDir(String extractedIncludeDir) {
        this.extractedIncludeDir = extractedIncludeDir;
    }

    void setGrpc(boolean grpc) { this.grpc = grpc; }

    public ScalaPBPluginExtension() {
        this.dependentProtoSources = new ArrayList<String>();
        this.protocVersion = "-v360";
        this.targetDir = "target/scala";
        this.projectProtoSourceDir = "src/main/protobuf";
        this.extractedIncludeDir = "target/external_protos";
        this.grpc = true;
    }
}
