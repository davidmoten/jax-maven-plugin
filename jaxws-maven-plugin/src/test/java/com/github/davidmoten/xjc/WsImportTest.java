package com.github.davidmoten.xjc;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import com.sun.tools.ws.WsImport;

public class WsImportTest {

    @Test
    public void testWsImport() throws Throwable {
        String[] args = new String[] {
          "-verbose", //
          "-d", "target", //
          "-s", "target", //
          new File("src/test/wsdl/sky-connect.wsdl").getAbsolutePath() //
        };
        assertEquals(0, WsImport.doMain(args));
    }
    
}
