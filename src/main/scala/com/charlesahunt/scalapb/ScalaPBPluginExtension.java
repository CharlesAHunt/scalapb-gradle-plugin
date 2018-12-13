package com.charlesahunt.scalapb;

import java.util.ArrayList;
import java.util.List;

public class ScalaPBPluginExtension {

    public List<String> dependentProtoSources;
    public String protocVersion;
    public String targetDir;
    public String projectProtoSourceDir;
    public String extractedIncludeDir;
    public Boolean grpc;
    public Boolean embeddedProtoc;

    public ScalaPBPluginExtension() {
        dependentProtoSources = new ArrayList<>();
        protocVersion = "-v360";
        targetDir = "target/scala";
        projectProtoSourceDir = "src/main/protobuf";
        extractedIncludeDir = "target/external_protos";
        grpc = true;
        embeddedProtoc = false;
    }
}
