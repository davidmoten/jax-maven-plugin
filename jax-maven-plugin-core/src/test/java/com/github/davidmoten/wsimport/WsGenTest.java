package com.github.davidmoten.wsimport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.github.davidmoten.jaxws.Example;
import com.sun.tools.ws.WsGen;

public class WsGenTest {

    @Test
    public void test() throws Throwable {
        String[] args = new String[] { "-verbose", "-keep", "-r", "target", "-d", "target", "-s", "target", "-wsdl",
                Example.class.getName() };
        assertEquals(0, WsGen.doMain(args));
        exists("target/com/github/davidmoten/jaxws/jaxws/GetIpAddress.class");
        exists("target/com/github/davidmoten/jaxws/jaxws/GetIpAddress.java");
        exists("target/ExampleService.wsdl");
        exists("target/ExampleService_schema1.xsd");
    }

    private static void exists(String filename) {
        assertTrue(new File(filename).exists());
    }

}
