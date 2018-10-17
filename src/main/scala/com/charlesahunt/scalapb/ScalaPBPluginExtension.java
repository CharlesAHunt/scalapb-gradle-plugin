package com.charlesahunt.scalapb;

import org.gradle.api.file.FileTreeElement;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.util.PatternFilterable;
import org.gradle.api.tasks.util.PatternSet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ScalaPBPluginExtension {
    /**
     * Things to explicitly include as dependent proto sources. These are explicitly recompiled into
     * scala. These may be .jar, .proto, .zip, almost any archive that could contain protos
     */
    List<String> externalProtoSources;

    /**
     * e.g. "-v360"
     */
    String protocVersion;

    /**
     * Place where generated case classes are placed
     */
    String targetDir;

    /**
     * This project's proto source dir. Analogous to srcDir in protobuf-gradle-plugin
     */
    String projectProtoSourceDir;

    /**
     * Place to unpack protos extracted from dependentProtoSources
     */
    String extractedIncludeDir;

    /**
     * Generate grpc stubs?
     */
    Boolean grpc;

    /**
     * Use embedded protoc?
     */
    Boolean embeddedProtoc;

    /**
     * Generate java conversion methods?
     */
    Boolean javaConversions;

    /**
     * When generating scala for proto dependencies of this project, only generate for dependent protos matching this spec
     */
    PatternSet dependencySpec;

    /**
     * If using protobuf-gradle-plugin, where are those outputs copied from (relative to project.projectDir)
     */
    String gradleProtobufExtractedPrefix;


    List<String> getExternalProtoSources() {
        return externalProtoSources;
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

    Boolean getEmbeddedProtoc() { return embeddedProtoc; }

    Boolean getJavaConversions() { return javaConversions; }

    PatternSet getDependencySpec() { return dependencySpec; }

    String getGradleProtobufExtractedPrefix() { return gradleProtobufExtractedPrefix; }

    void setExternalProtoSources(List<String> externalProtoSources) {
        this.externalProtoSources = externalProtoSources;
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

    void setEmbeddedProtoc(boolean embeddedProtoc) { this.embeddedProtoc = embeddedProtoc; }

    void setJavaConversions(boolean javaConversions) { this.javaConversions = javaConversions; }

    void setDependencySpec(PatternFilterable dependencySpec) { this.dependencySpec.copyFrom(dependencySpec); }

    void setGradleProtobufExtractedPrefix(String gradleProtobufExtractedPrefix) {
        this.gradleProtobufExtractedPrefix = gradleProtobufExtractedPrefix;
    }

    public ScalaPBPluginExtension() {
        this.externalProtoSources = new ArrayList<String>();
        this.protocVersion = "-v360";
        this.targetDir = "target/scala";
        this.projectProtoSourceDir = "src/main/protobuf";
        this.extractedIncludeDir = "target/external_protos";
        this.grpc = true;
        this.embeddedProtoc = false;
        this.javaConversions = false;
        // by default, generate everything. this is how specs are expected to work, but it will take a long time.
        this.dependencySpec = new PatternSet();
        // BY CONVENTION, this is usually build/extracted-protos/$sourceSet
        // where sourceSet is usually main
        this.gradleProtobufExtractedPrefix = "build/extracted-include-protos/main";
    }
}
