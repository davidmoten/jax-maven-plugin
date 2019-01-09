package com.github.davidmoten.xjc;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "xjc")
public class XjcMojo extends AbstractMojo {

    @Parameter(property = "argument")
    private List<String> arguments;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        Log log = getLog();
        log.info("Starting xjc mojo");
        
        ensureDestinationDirectoryExists();
        
        
    }

    private void ensureDestinationDirectoryExists() {
        Log log = getLog();
        for (int i = 0;i < arguments.size();i++) {
            if (arguments.get(i).trim().equals("-d") && i < arguments.size() - 1) {
                File dir = new File(arguments.get(i+1));
                if (!dir.exists()) {
                    log.info("destination directory (-d option) specified and does not exist, creating: " + dir);
                    dir.mkdirs();
                }
            }
        }
    }

}
