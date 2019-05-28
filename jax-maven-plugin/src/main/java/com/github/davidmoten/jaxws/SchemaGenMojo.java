package com.github.davidmoten.jaxws; 

import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "schemagen", requiresDependencyResolution = ResolutionScope.COMPILE)
public final class SchemaGenMojo extends BaseMojo implements HasClasspathScope {

    @Parameter(name = "classpathScope", defaultValue = "compile")
    private String classpathScope;

    @Parameter
    private List<String> sources;
    
    @Parameter
    private String jClasspath;
    
    @Parameter(name = "shortenClassPaths", defaultValue = "false")
    private String shortenClassPaths;

    public SchemaGenMojo() {
        super(JaxCommand.SCHEMAGEN);
    }

    @Override
    public String classpathScope() {
        return classpathScope;
    }

    public List<String> sources() {
        return sources == null ? new ArrayList<>() : sources;
    }
    
    public String jClasspath() {
        return jClasspath;
    }

    public String shortenClassPaths() {
        return shortenClassPaths;
    }

}