package com.github.davidmoten.jaxws;

public enum JaxCommand {

    WSGEN(WsGenMain.class), WSIMPORT(WsImportMain.class);

    private Class<?> mainClass;

    private JaxCommand(Class<?> mainClass) {
        this.mainClass = mainClass;
    }

    public Class<?> mainClass() {
        return mainClass;
    }

}
