package com.github.davidmoten.jaxws; 

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "schemagen")
public final class SchemaGenMojo extends BaseMojo implements HasClasspathScope {

    @Parameter(name = "classpathScope", defaultValue = "compile")
    private String classpathScope;

    public SchemaGenMojo() {
        super(JaxCommand.SCHEMAGEN);
    }

    @Override
    public String classpathScope() {
        return classpathScope;
    }

}