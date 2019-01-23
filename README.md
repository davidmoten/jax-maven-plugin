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
* supports JAXB extensions
* [unit tested](xjc-maven-plugin-test) on Oracle JDK 8, 9, 10, 11 and OpenJDK 10, 11 (using Travis)

## What about exec-maven-plugin?
It is possible to use *exec-maven-plugin* to call `xjc` via the `java` goal or `exec` goal to call `XJCFacade.main` (which calls `com.sun.tools.xjc.Driver.run`). There are problems though:

* With `exec:java` you can call `XJCFacade.main` having setup the classpath with the `jaxb-xjc` dependency **but** `XJCFacade.main` calls `System.exit` so that after generating your classes the maven build is killed. Obviously terrible when you want stuff to happen after generating the classes!
* With `exec:exec` you can call `XJCFacade.main` forked so that the `System.exit` doesn't kill the build **but** you have to build the classpath yourself (16 dependencies without maven to help you).
* With both of the `exec` options you need to add a call to `antrun-maven-plugin` to create the destination directory if it doesn't exist.

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

## Output
Here's sample output from the plugin:

```
[INFO] --- xjc-maven-plugin:0.1.3-SNAPSHOT:xjc (gen-from-dtd) @ xjc-maven-plugin-test ---
[INFO] Starting xjc mojo
[INFO] destination directory (-d option) specified and does not exist, creating: /home/dxm/Development/ide/eclipse/workspace-4.7/xjc-maven-plugin/xjc-maven-plugin-test/target/generated-sources/jaxb
[INFO] setting up classpath for jaxb-xjc version 2.4.0-b180830.0438
parsing a schema...
compiling a schema...
[INFO] generating code
unknown location

dummy/Exception.java
dummy/Frame.java
dummy/Log.java
dummy/ObjectFactory.java
dummy/Param.java
dummy/Record.java
[INFO] xjc mojo finished
```

Note that the `unknown location` line is associated with the `[INFO] generating code` line and can be ignored (it's supposed to report where in the input files an error is coming from but this INFO-level log line is not associated with a real problem so no location is included).

If you want more detail including all classpath items for the `xjc` call then call:

```bash
mvn clean install -X
```

## Arguments
See the Java 8 xjc [documentation](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/xjc.html) for descriptions of the arguments to pass to xjc using the plugin.

## Using JAXB extensions
If you add dependencies to the classpath used by `xjc` and include the `-extension` flag then you can customise the generated code. To add dependencies to the classpath of `xjc` use the `<dependencies>` element as below (let's add the [*jaxb2-basics*](https://github.com/highsource/jaxb2-basics/wiki/Using-JAXB2-Basics-Plugins) dependency):

```xml
<plugin>
    <groupId>com.github.davidmoten</groupId>
    <artifactId>xjc-maven-plugin</artifactId>
    <version>${project.parent.version}</version>
    <dependencies>
        <dependency>
            <groupId>org.jvnet.jaxb2_commons</groupId>
            <artifactId>jaxb2-basics</artifactId>
            <version>1.11.1</version>
        </dependency>
    </dependencies>
    <executions>
    ...
    </executions>
</plugin>
```

A fully worked example that generates `hashCode`, `equals` and `toString` methods in generated classes is in [xjc-maven-plugin-tests](xjc-maven-plugin-test/pom.xml).

A big thank you to @Glebcher601 for contributing extension support!

## Update the JAXB version used
For project maintainers, to use a new version of *jaxb-xjc* just update the `glassfish.jaxb.version` in the root pom.xml.
