package com.github.davidmoten.jaxws;

import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "wsimport")
public final class WsImportMojo extends BaseMojo {

    public WsImportMojo() {
        super(JaxCommand.WSIMPORT);
    }

}