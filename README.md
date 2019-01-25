# jax-maven-plugin
<a href="https://travis-ci.org/davidmoten/jax-maven-plugin"><img src="https://travis-ci.org/davidmoten/jax-maven-plugin.svg"/></a><br/>
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/jax-maven-plugin/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/jax-maven-plugin)

Status: *in development*

## Features
* supports `xjc`, `wsimport`, `wsgen`, `schemagen`
* supports Java 8, 9, 10, 11+ 
* detects the output directories and auto-creates if do not exist
* sets system properties
* sets JVM arguments
* optionally sets classpath using maven
* supports JAXB extensions
* [unit tested](jax-maven-plugin-test) on Oracle JDK 8, 9 and OpenJDK 10, 11 (using Travis)

## What about exec-maven-plugin?
It is possible to use *exec-maven-plugin* to call `xjc` via the `java` goal or `exec` goal to call `XJCFacade.main` (which calls `com.sun.tools.xjc.Driver.run`). There are problems though:

* With `exec:java` you can call `XJCFacade.main` having setup the classpath with the `jaxb-xjc` dependency **but** `XJCFacade.main` calls `System.exit` so that after generating your classes the maven build is killed. Obviously terrible when you want stuff to happen after generating the classes!
* With `exec:exec` you can call `XJCFacade.main` forked so that the `System.exit` doesn't kill the build **but** you have to build the classpath yourself (16 dependencies without maven to help you).
* With both of the `exec` options you need to add a call to `antrun-maven-plugin` to create the destination directory if it doesn't exist.

Instead of these poor options, use *jax-maven-plugin*!

## How to build
```
mvn clean install
```

## Getting started

Here's an example where we generate java classes with package `dummy` from an XSD file (pretty much a copy and paste from the [unit test project](jax-maven-plugin-test)):

```xml
<properties>
    <jaxb.generated>${project.build.directory}/generated-sources/jaxb</jaxb.generated>
</properties>
...
<plugin>
    <groupId>com.github.davidmoten</groupId>
    <artifactId>jax-maven-plugin</artifactId>
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
                            <jvmArguments>
                                <jvmArgument>-Xmx32m</jvmArgument>
                            </jvmArguments>
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
[INFO] --- jax-maven-plugin:0.1.4-SNAPSHOT:xjc (gen-from-dtd) @ jax-maven-plugin-test ---
[INFO] Starting XJC mojo
[INFO] destination directory (-d option) specified and does not exist, creating: /home/dave/workspace/jax-maven-plugin/jax-maven-plugin-test/target/generated-sources/jaxb
[INFO] setting up classpath for jaxws-tools version 0.1.4-SNAPSHOT
[INFO] call arguments:
  -----------------
  /opt/jdk/jdk-11.0.2/bin/java
  -classpath
  /home/dave/workspace/jax-maven-plugin/jax-maven-plugin-core/target/jax-maven-plugin-core-0.1.4-SNAPSHOT.jar:/home/dave/.m2/repository/com/sun/xml/ws/jaxws-tools/2.3.1/jaxws-tools-2.3.1.jar:/home/dave/.m2/repository/com/sun/xml/ws/jaxws-rt/2.3.1/jaxws-rt-2.3.1.jar:/home/dave/.m2/repository/javax/xml/bind/jaxb-api/2.3.1/jaxb-api-2.3.1.jar:/home/dave/.m2/repository/javax/activation/javax.activation-api/1.2.0/javax.activation-api-1.2.0.jar:/home/dave/.m2/repository/javax/xml/ws/jaxws-api/2.3.1/jaxws-api-2.3.1.jar:/home/dave/.m2/repository/javax/xml/soap/javax.xml.soap-api/1.4.0/javax.xml.soap-api-1.4.0.jar:/home/dave/.m2/repository/javax/annotation/javax.annotation-api/1.3.2/javax.annotation-api-1.3.2.jar:/home/dave/.m2/repository/javax/jws/javax.jws-api/1.1/javax.jws-api-1.1.jar:/home/dave/.m2/repository/org/glassfish/jaxb/jaxb-runtime/2.3.1/jaxb-runtime-2.3.1.jar:/home/dave/.m2/repository/org/glassfish/jaxb/txw2/2.3.1/txw2-2.3.1.jar:/home/dave/.m2/repository/com/sun/istack/istack-commons-runtime/3.0.7/istack-commons-runtime-3.0.7.jar:/home/dave/.m2/repository/org/jvnet/staxex/stax-ex/1.8/stax-ex-1.8.jar:/home/dave/.m2/repository/com/sun/xml/fastinfoset/FastInfoset/1.2.15/FastInfoset-1.2.15.jar:/home/dave/.m2/repository/com/sun/xml/ws/policy/2.7.5/policy-2.7.5.jar:/home/dave/.m2/repository/com/sun/activation/javax.activation/1.2.0/javax.activation-1.2.0.jar:/home/dave/.m2/repository/org/glassfish/gmbal/gmbal-api-only/3.1.0-b001/gmbal-api-only-3.1.0-b001.jar:/home/dave/.m2/repository/org/glassfish/external/management-api/3.0.0-b012/management-api-3.0.0-b012.jar:/home/dave/.m2/repository/com/sun/xml/stream/buffer/streambuffer/1.5.6/streambuffer-1.5.6.jar:/home/dave/.m2/repository/org/jvnet/mimepull/mimepull/1.9.10/mimepull-1.9.10.jar:/home/dave/.m2/repository/org/glassfish/ha/ha-api/3.1.9/ha-api-3.1.9.jar:/home/dave/.m2/repository/com/sun/xml/messaging/saaj/saaj-impl/1.5.0/saaj-impl-1.5.0.jar:/home/dave/.m2/repository/com/fasterxml/woodstox/woodstox-core/5.1.0/woodstox-core-5.1.0.jar:/home/dave/.m2/repository/org/codehaus/woodstox/stax2-api/4.1/stax2-api-4.1.jar:/home/dave/.m2/repository/com/sun/xml/bind/jaxb-xjc/2.3.1/jaxb-xjc-2.3.1.jar:/home/dave/.m2/repository/com/sun/xml/bind/jaxb-jxc/2.3.1/jaxb-jxc-2.3.1.jar:/home/dave/.m2/repository/org/jvnet/jaxb2_commons/jaxb2-basics/1.11.1/jaxb2-basics-1.11.1.jar:/home/dave/.m2/repository/org/jvnet/jaxb2_commons/jaxb2-basics-runtime/1.11.1/jaxb2-basics-runtime-1.11.1.jar:/home/dave/.m2/repository/org/jvnet/jaxb2_commons/jaxb2-basics-tools/1.11.1/jaxb2-basics-tools-1.11.1.jar:/home/dave/.m2/repository/org/slf4j/slf4j-api/1.7.7/slf4j-api-1.7.7.jar:/home/dave/.m2/repository/commons-beanutils/commons-beanutils/1.9.2/commons-beanutils-1.9.2.jar:/home/dave/.m2/repository/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar:/home/dave/.m2/repository/org/slf4j/jcl-over-slf4j/1.7.7/jcl-over-slf4j-1.7.7.jar:/home/dave/.m2/repository/org/apache/commons/commons-lang3/3.2.1/commons-lang3-3.2.1.jar:/home/dave/.m2/repository/com/google/code/javaparser/javaparser/1.0.11/javaparser-1.0.11.jar
  -Xms32m
  -DenableExternalEntityProcessing=true
  com.github.davidmoten.jaxws.XjcMain
  -verbose
  -d
  /home/dave/workspace/jax-maven-plugin/jax-maven-plugin-test/target/generated-sources/jaxb
  -p
  dummy
  -dtd
  /home/dave/workspace/jax-maven-plugin/jax-maven-plugin-test/src/main/dtd/logger.dtd
  -----------------
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
[INFO] XJC mojo finished
```

Note that the `unknown location` line is associated with the `[INFO] generating code` line and can be ignored (it's supposed to report where in the input files an error is coming from but this INFO-level log line is not associated with a real problem so no location is included).

If you want more detail including all classpath items for the `xjc` or other call then call:

```bash
mvn clean install -X
```

## Arguments
See the Java 8 [documentation](https://docs.oracle.com/javase/8/docs/technotes/tools/#webservices) for descriptions of the arguments to pass to tools using the plugin.

Note that if you don't specify the `-d` destination directory then generated source will be placed in the root of the project directory. In short, always specify the `-d` parameter.

## Using JAXB extensions
If you add dependencies to the classpath used by `xjc` and include the `-extension` flag then you can customise the generated code. To add dependencies to the classpath of `xjc` use the `<dependencies>` element as below (let's add the [*jaxb2-basics*](https://github.com/highsource/jaxb2-basics/wiki/Using-JAXB2-Basics-Plugins) dependency):

```xml
<plugin>
    <groupId>com.github.davidmoten</groupId>
    <artifactId>jax-maven-plugin</artifactId>
    <version>VERSION_HERE</version>
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

A fully worked example that generates `hashCode`, `equals` and `toString` methods in generated classes is in [jax-maven-plugin-tests](jax-maven-plugin-test/pom.xml).

A big thank you to @Glebcher601 for contributing extension support!

## Update the JAXB version used
For project maintainers, to use a new version of *jaxb* just update the `jaxb.version` in the root pom.xml.
