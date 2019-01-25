package com.github.davidmoten.jaxws;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class JaxwsMavenPluginTest {
    
    @Test
    public void testWsImportGeneratedFilesExist() {
        assertTrue(new File("target/generated-sources/jaxws/com/examples/wsdl/helloservice/HelloService.java").exists());
        assertTrue(new File("target/generated-classes/jaxws/com/examples/wsdl/helloservice/HelloService.class").exists());
    }
    
    @Test
    public void testWsImportGeneratedSourcesOnClasspath() throws ClassNotFoundException {
        Class.forName("com.examples.wsdl.helloservice.HelloService");
    }
    
    @Test
    public void testWsGenGeneratedSourcesOnClasspath() throws ClassNotFoundException {
        Class.forName("com.github.davidmoten.jaxws.jaxws.GetIpAddress");
    }

}
