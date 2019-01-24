package com.github.davidmoten.jaxws;

public enum JaxwsCommand {

    WSGEN(WsGenMain.class), WSIMPORT(WsImportMain.class);

    private Class<?> mainClass;

    private JaxwsCommand(Class<?> mainClass) {
        this.mainClass = mainClass;
    }

    public Class<?> mainClass() {
        return mainClass;
    }

}
