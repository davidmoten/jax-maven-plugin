package com.github.davidmoten.xjc;

import org.junit.Test;

import dummy.ObjectFactory;

public class XjcMojoTest {
    
    @Test
    public void testClassesGeneratedFromDtd() {
       System.out.println("Found generated class " + ObjectFactory.class.getName());
    }

}
