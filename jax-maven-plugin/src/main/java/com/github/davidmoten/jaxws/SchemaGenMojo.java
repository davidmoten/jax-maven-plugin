package com.github.davidmoten.jaxws; 

import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "schemagen")
public final class SchemaGenMojo extends BaseMojo implements HasClasspathScope {

    @Parameter(name = "classpathScope", defaultValue = "compile")
    private String classpathScope;

    @Parameter
    private List<String> sources;

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

}