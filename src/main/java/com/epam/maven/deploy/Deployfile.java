package com.epam.maven.deploy;

import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Vitaliy Boyarsky
 */
public class Deployfile {

    private static final String CURRENT_VERSION = "1.7.3";
    private static final Pattern SUPPORTED_VERSION = Pattern.compile("1\\.7\\.[1-3]");

    @Getter
    @JsonProperty("version")
    private String version;

    @Getter
    @Setter
    @JsonProperty("scripts")
    private List<Script> scripts;

    public static Deployfile readDeployfile(File deployfile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(deployfile, Deployfile.class);
    }

    public void setVersion(String version) {
        if (!SUPPORTED_VERSION.matcher(version).matches())
            throw new IllegalArgumentException("Version of Deployfile not supported. Current version is [" + CURRENT_VERSION + "]");
        this.version = version;
    }

    public Script scriptOf(String name) throws MojoExecutionException {
        for (Script script : scripts)
            if (script.getName().equals(name))
                return script;
        throw new MojoExecutionException("Script with name '" + name + "' not found. Please check you configuration");
    }

}
