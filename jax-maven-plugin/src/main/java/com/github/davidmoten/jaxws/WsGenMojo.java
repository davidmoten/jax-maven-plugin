package com.github.davidmoten.jaxws;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "wsgen")
public final class WsGenMojo extends BaseMojo implements HasClasspathScope {

    @Parameter(name = "classpathScope", defaultValue = "compile")
    private String classpathScope;

    public WsGenMojo() {
        super(JaxCommand.WSGEN);
    }

    @Override
    public String classpathScope() {
        return classpathScope;
    }

}
