package com.github.davidmoten.xjc;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

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
