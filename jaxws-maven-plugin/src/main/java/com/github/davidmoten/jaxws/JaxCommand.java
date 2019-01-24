package com.github.davidmoten.jaxws;

import java.util.Set;

import com.github.davidmoten.guavamini.Sets;

enum JaxCommand {

    WSGEN(WsGenMain.class, "jaxws-maven-plugin-core", Sets.newHashSet("-r", "-d", "-s")), //
    WSIMPORT(WsImportMain.class, "jaxws-maven-plugin-core", Sets.newHashSet("-d", "-s")), //
    XJC(XjcMain.class, "jaxws-maven-plugin-core", Sets.newHashSet("-d"));

    private final Class<?> mainClass;
    private final String artifactId;
    private final Set<String> directoryParameters;

    private JaxCommand(Class<?> mainClass, String artifactId, Set<String> directoryParameters) {
        this.mainClass = mainClass;
        this.artifactId = artifactId;
        this.directoryParameters = directoryParameters;
    }

    public Class<?> mainClass() {
        return mainClass;
    }

    public String mainClassArtifactId() {
        return artifactId;
    }

    public Set<String> getDirectoryParameters() {
        return directoryParameters;
    }
    
}
