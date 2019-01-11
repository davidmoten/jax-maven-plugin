package com.github.davidmoten.xjc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import com.sun.tools.xjc.Driver;

public class XjcDriverTest {

    @Test
    public void testDriverRunWithPersonSchemaWithBindings() throws Exception {
        // System.setProperty("enableExternalEntityProcessing", "true");
        String bindings = new File("src/test/jaxb/test1/bindings").getAbsolutePath();
        String xsd = new File("src/test/jaxb/test1/xsd").getAbsolutePath();
        String[] args = new String[] { //
                "-verbose", //
                "-d", "target", //
                xsd, //
                "-b", bindings //
        };

        assertEquals(0, Driver.run(args, System.out, System.out));
        assertTrue(new File("target/pkg/ObjectFactory.java").exists());
        assertTrue(new File("target/pkg/Person.java").exists());
        assertTrue(new File("target/pkg2/Place.java").exists());
    }
    
}
