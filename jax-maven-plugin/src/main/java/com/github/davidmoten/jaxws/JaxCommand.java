package com.github.davidmoten.jaxws;

import java.util.Optional;
import java.util.Set;

import com.github.davidmoten.guavamini.Sets;

enum JaxCommand {

    WSGEN(WsGenMain.class, Sets.newHashSet("-r", "-d", "-s"), Optional.of("-s"), Optional.of("-r")), //
    WSIMPORT(WsImportMain.class, Sets.newHashSet("-d", "-s"), Optional.of("-s"), Optional.empty()), //
    XJC(XjcMain.class, Sets.newHashSet("-d"), Optional.of("-d"), Optional.empty()), //
    SCHEMAGEN(SchemaGenMain.class, Sets.newHashSet("-d"), Optional.of("-d"), Optional.empty());

    private final Class<?> mainClass;
    private final Set<String> directoryParameters;
    private final Optional<String> sourceParameter;
    private final Optional<String> resourceParameter;

    private JaxCommand(Class<?> mainClass, Set<String> directoryParameters, Optional<String> sourceParameter,
            Optional<String> resourceParameter) {
        this.mainClass = mainClass;
        this.directoryParameters = directoryParameters;
        this.sourceParameter = sourceParameter;
        this.resourceParameter = resourceParameter;
        sourceParameter.ifPresent(x -> {
            if (!directoryParameters.contains(x)) {
                throw new IllegalArgumentException();
            }
        });
        resourceParameter.ifPresent(x -> {
            if (!directoryParameters.contains(x)) {
                throw new IllegalArgumentException();
            }
        });
    }

    Class<?> mainClass() {
        return mainClass;
    }

    Set<String> getDirectoryParameters() {
        return directoryParameters;
    }

    public Optional<String> getSourceParameter() {
        return sourceParameter;
    }

    public Optional<String> getResourceParameter() {
        return resourceParameter;
    }

}
