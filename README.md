# xjc-maven-plugin
<a href="https://travis-ci.org/davidmoten/xjc-maven-plugin"><img src="https://travis-ci.org/davidmoten/xjc-maven-plugin.svg"/></a><br/>
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/xjc-maven-plugin/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/xjc-maven-plugin)

The `xjc` executable is not present in the JDK as of version 11. However, the functionality of `xjc` is still available (right down to the command line arguments to that original executable) via external artifacts like *org.glassfish.jaxb:jaxb-xjc*.

*xjc-maven-plugin* sets up the classpath dependencies and passes the arguments you provide directly through to the `com.sun.tools.xjc.Driver.run` method.

The development of this plugin was motivated by [problems](https://github.com/mojohaus/jaxb2-maven-plugin/issues/43) with the *jaxb2-maven-plugin* with Java 9+. My company's codebase used *jaxb2-maven-plugin* in ~10 locations (always the `xjc` goal) and we wanted to move to OpenJDK 11+ given the EOL (unpaid) for Oracle Java 8 in January 2019.

## Features
* Supports Java 8, 9, 10, 11+, generates code from DTD or XSD
* detects the `-d` destination directoy and auto-creates directory if does not exist
* sets system properties
* [unit tested](xjc-maven-plugin-test) on Java 8, 9, 10, 11 (using Travis)

Status: *deployed to Maven Central*

## How to build
```
mvn clean install
```

### Getting started

Here's an example where we generate java classes with package `dummy` from a DTD file:

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
                <arguments>
                    <argument>-verbose</argument>
                    <argument>-d</argument>
                    <argument>${jaxb.generated}</argument>
                    <argument>-p</argument>
                    <argument>dummy</argument>
                    <argument>-dtd</argument>
                    <argument>${project.basedir}/src/main/dtd/logger.dtd</argument>
                </arguments>
            </configuration>
        </execution>
    </executions>
</plugin>
<!-- add the generated sources to source -->
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
