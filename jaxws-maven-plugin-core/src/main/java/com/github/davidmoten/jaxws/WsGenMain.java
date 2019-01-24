package com.github.davidmoten.jaxws;

import com.sun.tools.ws.WsGen;

public final class WsGenMain {
   
    public static void main(String[] args) throws Throwable {
        int returnCode = WsGen.doMain(args);
        if (returnCode != 0) {
            throw new Exception("WsGen.doMain failed with returnCode=" + returnCode + ". See logs above for details");
        }
    }
    
}
