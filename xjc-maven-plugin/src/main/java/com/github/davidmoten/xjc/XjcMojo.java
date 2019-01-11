package com.github.davidmoten.xjc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Mojo(name = "xjc")
public final class XjcMojo extends AbstractMojo {

    @Parameter(required = true, name = "arguments")
    private List<String> arguments;

    @Parameter(name = "systemProperties")
    private Map<String, String> systemProperties;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        log.info("Starting xjc mojo");

        ensureDestinationDirectoryExists();

        List<String> command = createCommand();

        try {
            new ProcessExecutor() //
                    .command(command) //
                    .exitValueNormal() //
                    .redirectOutput(System.out) //
                    .redirectError(System.out) //
                    .execute();
        } catch (InvalidExitValueException | IOException | InterruptedException | TimeoutException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        log.info("xjc mojo finished");
    }

    private List<String> createCommand() {

        // get the current classpath (plugin classpath) and just hang on to the deps
        // required for jaxb-xjc

        // TODO use some sort of maven resolver component to find the deps of jaxb-xjc
        
        Log log = getLog();
        String v = "-2.4.0-b180830.0438.jar";
        Set<String> filenames = Sets.newHashSet("jaxb-xjc" + v, //
                "jaxb-runtime" + v, //
                "xsom" + v, //
                "relaxng-datatype" + v, //
                "codemodel" + v, //
                "rngom" + v, //
                "dtd-parser-1.4.jar", //
                "istack-commons-tools-3.0.7.jar", //
                "ant-1.10.2.jar", //
                "ant-launcher-1.10.2.jar", //
                "istack-commons-runtime-3.0.7.jar", //
                "jaxb-api-2.4.0-b180830.0359.jar", //
                "javax.activation-api-1.2.0.jar", //
                "txw2" + v, //
                "stax-ex-1.8.jar", //
                "FastInfoset-1.2.15.jar" //
        );

        final URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        StringBuilder classpath = new StringBuilder();
        for (final URL url : classLoader.getURLs()) {
            File file = new File(url.getFile());
            log.debug("classpath entry: " + file.getName());
            if (filenames.contains(file.getName()) || file.getName().startsWith("xjc-maven-plugin-core")) {
                if (classpath.length() > 0) {
                    classpath.append(File.pathSeparator);
                }
                classpath.append(file.getAbsolutePath());
            }
        }
        log.debug("classpath=" + classpath);

        final String javaExecutable = System.getProperty("java.home") + File.separator + "bin" + File.separator
                + "java";
        List<String> command = Lists.newArrayList( //
                javaExecutable, //
                "-classpath", //
                classpath.toString());
        if (systemProperties != null) {
            command.addAll(systemProperties //
                    .entrySet() //
                    .stream() //
                    .map(entry -> "-D" + entry.getKey() + "=" + entry.getValue()) //
                    .collect(Collectors.toList()));
        }
        command.add(DriverMain.class.getName());
        command.addAll(arguments);
        return command;
    }

    private void ensureDestinationDirectoryExists() {
        for (int i = 0; i < arguments.size(); i++) {
            if (arguments.get(i).trim().equals("-d") && i < arguments.size() - 1) {
                File dir = new File(arguments.get(i + 1));
                if (!dir.exists()) {
                    getLog().info("destination directory (-d option) specified and does not exist, creating: " + dir);
                    dir.mkdirs();
                }
            }
        }
    }

}
