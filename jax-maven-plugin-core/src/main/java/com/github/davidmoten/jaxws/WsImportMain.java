package com.github.davidmoten.jaxws;

import com.sun.tools.ws.WsImport;

public final class WsImportMain {

    public static void main(String[] args) throws Throwable {
        int returnCode = WsImport.doMain(args);
        if (returnCode != 0) {
            throw new Exception("WsImport.doMain failed with returnCode=" + returnCode + ". See logs above for details");
        }
    }

}
