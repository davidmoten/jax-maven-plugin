# xjc-maven-plugin
<a href="https://travis-ci.org/davidmoten/xjc-maven-plugin"><img src="https://travis-ci.org/davidmoten/xjc-maven-plugin.svg"/></a><br/>
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/xjc-maven-plugin/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/xjc-maven-plugin)

The `xjc` executable is not present in the JDK as of version 11. However, the functionality of `xjc` is still available (right down to the command line arguments to that original executable) via external artifacts like *org.glassfish.jaxb:jaxb-xjc*.

*xjc-maven-plugin* sets up the classpath dependencies and passes the arguments you provide directly through to the `com.sun.tools.xjc.Driver.run` method.

The development of this plugin was motivated by [problems](https://github.com/mojohaus/jaxb2-maven-plugin/issues/43) with the *jaxb2-maven-plugin* with Java 9+. My company's codebase used *jaxb2-maven-plugin* in ~10 locations (always the `xjc` goal) and we wanted to move to OpenJDK 11+ given the EOL (unpaid) for Oracle Java 8 in January 2019.

Status: *deployed to Maven Central*

## Features
* Supports Java 8, 9, 10, 11+, generates code from DTD or XSD
* detects the `-d` destination directoy and auto-creates directory if does not exist
* sets system properties
* [unit tested](xjc-maven-plugin-test) on Oracle JDK 8, 9, 10, 11 and OpenJDK 10, 11 (using Travis)

## What about exec-maven-plugin?
It is possible to use *exec-maven-plugin* to call `xjc` via the `java` goal or `exec` goal to call `XJCFacade.main`. There are problems though:

* With `exec:java` you can call `XJCFacade.main` having setup the classpath with the `jaxb-xjc` dependency **but** `XJCFacade.main` calls `System.exit` so that after generating your classes the maven build is killed. Obviously terrible when you want stuff to happen after generating the classes!
* With `exec:exec` you can call `XJCFacade.main` forked so that the `System.exit` doesn't kill the build **but** you have to build the classpath yourself (16 dependencies without maven to help you).

Instead of these poor options, use *xjc-maven-plugin*!

## How to build
```
mvn clean install
```

## Getting started

Here's an example where we generate java classes with package `dummy` from an XSD file (pretty much a copy and paste from the [unit test project](xjc-maven-plugin-test)):

```xml
<properties>
    <jaxb.generated>${project.build.directory}/generated-sources/jaxb</jaxb.generated>
</properties>
...
<plugin>
    <groupId>com.github.davidmoten</groupId>
    <artifactId>xjc-maven-plugin</artifactId>
    <version>VERSION_HERE</version>
    <executions>
        <execution>
            <id>gen</id>
            <!-- generate sources from the java.util.logging DTD -->
            <phase>generate-sources</phase>
            <goals>
                <goal>xjc</goal>
            </goals>
            <configuration>
                <systemProperties>
                    <enableExternalEntityProcessing>true</enableExternalEntityProcessing>
                </systemProperties>
                <execution>
                        <id>gen-from-xsd-kml</id>
                        <!-- generate sources from the kml 2.2 xsd -->
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>xjc</goal>
                        </goals>
                        <configuration>
                            <systemProperties>
                                <enableExternalEntityProcessing>true</enableExternalEntityProcessing>
                            </systemProperties>
                            <arguments>
                                <!-- These are the arguments you would normally 
                                     have used with a call to xjc -->
                                <argument>-verbose</argument>
                                <!-- set the directory to hold generated classees -->
                                <argument>-d</argument>
                                <argument>${jaxb.generated}</argument>
                                <! -- set the directory containing schemas to convert to classes -->
                                <argument>${project.basedir}/src/main/jaxb/kml-2-2/xsd</argument>
                                <!-- set the directory containing jaxb bindings (customizations) -->
                                <argument>-b</argument>
                                <argument>${project.basedir}/src/main/jaxb/kml-2-2/bindings</argument>
                            </arguments>
                        </configuration>
                    </execution>
            </configuration>
        </execution>
    </executions>
</plugin>
<!-- ensure the generated sources are on the classpath (and in built jar) -->
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <version>3.0.0</version>
    <executions>
        <execution>
            <id>add-source</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>add-source</goal>
            </goals>
            <configuration>
                <sources>
                    <source>${jaxb.generated}</source>
                </sources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## Arguments
See the Java 8 xjc [documentation](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/xjc.html) for descriptions of the arguments to pass to xjc using the plugin.
