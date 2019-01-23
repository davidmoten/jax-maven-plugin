package com.github.davidmoten.xjc;

import org.junit.Assert;
import org.junit.Test;

import dummy.ObjectFactory;
import dummy.Param;
import dummy.Record;

import java.lang.reflect.Method;

public class XjcPluginTest {
    
    @Test
    public void testClassesGeneratedFromDtd() {
       System.out.println("Found generated class " + ObjectFactory.class.getName());
       System.out.println("Found generated class " + Param.class.getName());
       System.out.println("Found generated class " + Record.class.getName());
    }

    @Test
    public void testJaxb2PluginGeneratedCodePresent() {
        Assert.assertNotNull(getDeclaredMethod(plugin.support.Param.class, "toString"));
        Assert.assertNotNull(getDeclaredMethod(plugin.support.Param.class, "equals"));
        Assert.assertNotNull(getDeclaredMethod(plugin.support.Param.class, "hashCode"));
        System.out.println("Jaxb2-basics plugins worked correctly");
    }

    @Test
    public void testKmlEpisodeFileGenerated() {
        Assert.assertNotNull(XjcPluginTest.class.getClassLoader().getResourceAsStream("kml.episode"));
        System.out.println("episode file generated correctly");
    }

    private Method getDeclaredMethod(Class<?> clazz, String methodName)
    {
        for (Method method : clazz.getDeclaredMethods()) {
          if(method.getName().equals(methodName)) {
            return method;
          }
        }

        throw new RuntimeException(new NoSuchMethodException(methodName));
    }
}
