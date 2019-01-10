package com.github.davidmoten.xjc;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

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

        // Artifact a = project //
        // .getArtifacts() //
        // .stream() //
        // .filter(x -> x.getGroupId().equals("org.glassfish.jaxb") &&
        // x.getArtifactId().equals("jaxb-xjc")) //
        // .findFirst() //
        // .get();
        //

        // System.out.println("dependencyTrail=" + a.getDependencyTrail());

        String v = "-2.4.0-b180830.0438.jar";
        Set<String> filenames = Sets.newHashSet("jaxb-xjc" + v, //
                "jaxb-runtime" + v, //
                "xsom" + v, //
                "relaxng-datatype" + v, //
                "codemodel" + v, //
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
        Set<String> found = Sets.newHashSet();
        for (final URL url : classLoader.getURLs()) {
            File file = new File(url.getFile());
            log.info("classpath entry: " + file.getName());
            if (filenames.contains(file.getName())) {
                found.add(file.getName());
                if (classpath.length() > 0) {
                    classpath.append(File.pathSeparator);
                }
                classpath.append(file.getAbsolutePath());
            }
        }
        if ( filenames.size() != found.size()) {
            Set<String> set = new HashSet<String>(filenames);
            set.removeAll(found);
            throw new MojoExecutionException(
                    "did not find all expected classpath. Missing " + set);
        }
        log.info("classpath=" + classpath);

        log.info("Starting xjc mojo");

        setSystemProperties();

        ensureDestinationDirectoryExists();

        callXjc();

        log.info("xjc mojo finished");
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

    private void setSystemProperties() {
        if (systemProperties != null) {
            getLog().info("setting system properties: " + systemProperties);
            for (Entry<String, String> entry : systemProperties.entrySet()) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    private void callXjc() throws MojoExecutionException {
        try {
            getLog().info("running com.sun.tools.xjc.Driver.run method with arguments:\n" //
                    + arguments //
                            .stream() //
                            .map(x -> "  " + x) //
                            .collect(Collectors.joining("\n")));
            if (com.sun.tools.xjc.Driver.run(arguments.toArray(new String[] {}), System.out, System.out) != 0) {
                throw new MojoExecutionException("xjc failed, see log above for details");
            }
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

}
