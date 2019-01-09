# xjc-maven-plugin
The `xjc` executable is not present in the JDK as of version 11. The functionality of `xjc` is still available (right down to the command line arguments to that original executable) via the method call `com.sun.tools.xjc.Driver.run`. *xjc-maven-plugin* sets up the classpath dependencies and passes the arguments you provide directly through to the mentioned `run` method.

* Supports Java 8, 9, 10, 11+, generates code from DTD or XSD
* detects the `-d` destination directoy and auto-creates directory if does not exist

Status: *deployed to Maven Central*

## How to build
```
mvn clean install
```

### Getting started

This xml fragment is an example of a call to the *xjc-maven-plugin*:

```xml
<plugin>
    <groupId>com.github.davidmoten</groupId>
    <artifactId>xjc-maven-plugin</artifactId>
    <version>VERSION_HERE</version>
    <executions>
        <execution>
            <id>gen</id>
            <!-- generate sources from the java.util.logging 
                DTD -->
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
