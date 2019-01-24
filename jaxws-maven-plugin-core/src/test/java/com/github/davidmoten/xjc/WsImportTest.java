package com.github.davidmoten.xjc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.sun.tools.ws.WsImport;

public class WsImportTest {

    @Test
    public void testWsImport() throws Throwable {
        File wsdl = new File("src/test/wsdl/hello-service.wsdl");
        assertTrue(wsdl.exists());
        String[] args = new String[] {
          "-verbose", //
          "-extension", //
          "-keep", //
          "-d", "target", //
          "-s", "target", //
          wsdl.getAbsolutePath() //
        };
        assertEquals(0, WsImport.doMain(args));
        assertTrue(new File("target/com/examples/wsdl/helloservice/HelloService.class").exists());
        assertTrue(new File("target/com/examples/wsdl/helloservice/HelloService.java").exists());
    }
    
}
