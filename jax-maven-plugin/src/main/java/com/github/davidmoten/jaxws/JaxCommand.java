package com.github.davidmoten.jaxws;

import java.util.Set;

import com.github.davidmoten.guavamini.Sets;

enum JaxCommand {

    WSGEN(WsGenMain.class, Sets.newHashSet("-r", "-d", "-s")), //
    WSIMPORT(WsImportMain.class, Sets.newHashSet("-d", "-s")), //
    XJC(XjcMain.class, Sets.newHashSet("-d")), //
    SCHEMAGEN(SchemaGenMain.class, Sets.newHashSet("-d"));

    private final Class<?> mainClass;
    private final Set<String> directoryParameters;

    private JaxCommand(Class<?> mainClass, Set<String> directoryParameters) {
        this.mainClass = mainClass;
        this.directoryParameters = directoryParameters;
    }

    Class<?> mainClass() {
        return mainClass;
    }

    Set<String> getDirectoryParameters() {
        return directoryParameters;
    }
    
}
