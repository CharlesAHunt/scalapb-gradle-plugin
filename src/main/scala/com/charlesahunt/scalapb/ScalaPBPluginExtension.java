package com.charlesahunt.scalapb;

import java.util.ArrayList;
import java.util.List;

public class ScalaPBPluginExtension {

    List<String> dependentProtoSources;
    String targetDir;
    String projectProtoSourceDir;
    String extractedIncludeDir;
    Boolean grpc;
    Boolean embeddedProtoc;

    List<String> getDependentProtoSources() {
        return dependentProtoSources;
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

    Boolean getEmbeddedProtoc() { return embeddedProtoc; }

    void setDependentProtoSources(List<String> dependentProtoSources) {
        this.dependentProtoSources = dependentProtoSources;
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

    void setEmbeddedProtoc(boolean embeddedProtoc) { this.embeddedProtoc = embeddedProtoc; }

    public ScalaPBPluginExtension() {
        this.dependentProtoSources = new ArrayList<String>();
        this.targetDir = "target/scala";
        this.projectProtoSourceDir = "src/main/protobuf";
        this.extractedIncludeDir = "target/external_protos";
        this.grpc = true;
        this.embeddedProtoc = false;
    }
}
