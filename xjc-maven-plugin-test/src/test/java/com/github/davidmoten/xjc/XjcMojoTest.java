package com.github.davidmoten.xjc;

import org.junit.Test;

import dummy.ObjectFactory;
import dummy.Param;
import dummy.Record;

public class XjcMojoTest {
    
    @Test
    public void testClassesGeneratedFromDtd() {
       System.out.println("Found generated class " + ObjectFactory.class.getName());
       System.out.println("Found generated class " + Param.class.getName());
       System.out.println("Found generated class " + Record.class.getName());
    }

}
