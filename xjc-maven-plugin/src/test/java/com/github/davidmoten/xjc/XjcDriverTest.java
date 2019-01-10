package com.github.davidmoten.xjc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.sun.tools.xjc.Driver;

public class XjcDriverTest {

    @Test
    public void testDriverRunWithBindings() throws Exception {
//        System.setProperty("enableExternalEntityProcessing", "true");
        String bindings = new File("src/test/bindings").getAbsolutePath();
        String xsd = new File("src/test/xsd").getAbsolutePath();
        String[] args = new String[] {
                "-verbose", //
                "-b", bindings, //
                "-d", "target", //
                xsd, //
        };

        assertEquals(0, Driver.run(args, System.out, System.out));
        assertTrue(new File("target/pkg/ObjectFactory.java").exists());
    }

}
