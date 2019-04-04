package com.github.davidmoten.jaxws;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "xjc", requiresDependencyResolution = ResolutionScope.COMPILE)
public final class XjcMojo extends BaseMojo {

    public XjcMojo() {
        super(JaxCommand.XJC);
    }

}