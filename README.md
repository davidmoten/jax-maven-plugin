# xjc-maven-plugin
The `xjc` executable is not present in the JDK as of version 11. The functionality of `xjc` is still available (right down to the command line arguments to that original executable) via the method call `com.sun.tools.xjc.Driver.run`. *xjc-maven-plugin* sets up the classpath dependencies and passes the arguments you provide directly through to the mentioned `run` method.

* Supports Java 8, 9, 10, 11+, generates code from DTD or XSD
* detects the `-d` destination directoy and auto-creates directory if does not exist
