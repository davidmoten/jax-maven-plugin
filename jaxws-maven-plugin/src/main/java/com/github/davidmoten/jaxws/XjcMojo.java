package com.github.davidmoten.jaxws;

import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "xjc")
public final class XjcMojo extends BaseMojo {

    public XjcMojo() {
        super(JaxCommand.XJC);
    }

}