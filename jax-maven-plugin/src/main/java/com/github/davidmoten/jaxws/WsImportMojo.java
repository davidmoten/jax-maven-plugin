package com.github.davidmoten.jaxws;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "wsimport", requiresDependencyResolution = ResolutionScope.COMPILE)
public final class WsImportMojo extends BaseMojo {

    public WsImportMojo() {
        super(JaxCommand.WSIMPORT);
    }

}