package com.github.davidmoten.xjc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.repository.RepositorySystem;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;

import com.google.common.collect.Lists;

@Mojo(name = "xjc")
public final class XjcMojo extends AbstractMojo {

    // TODO should match version from pom.xml!
    private static final String JAXB_XJC_VERSION = "2.4.0-b180830.0438";// "2.4.0-b180830.0438";

    @Parameter(required = true, name = "arguments")
    private List<String> arguments;

    @Parameter(name = "systemProperties")
    private Map<String, String> systemProperties;

    @Component
    private RepositorySystem repositorySystem;

    @Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
    private ArtifactRepository localRepository;

    @Parameter(defaultValue = "${remoteArtifactRepositories}", readonly = true, required = true)
    private List<ArtifactRepository> remoteRepositories;

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
        } catch (InvalidExitValueException | IOException | InterruptedException
                | TimeoutException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        log.info("xjc mojo finished");
    }

    private List<String> createCommand() {

        // https://stackoverflow.com/questions/1440224/how-can-i-download-maven-artifacts-within-a-plugin

        Log log = getLog();

        ////////////////////////////////////////////////////////
        //
        // get the classpath entries for the deps of jaxb-xjc
        //
        ////////////////////////////////////////////////////////

        Artifact artifact = repositorySystem.createArtifact( //
                "org.glassfish.jaxb", "jaxb-xjc", JAXB_XJC_VERSION, "", "jar");

        log.info("setting up classpath for jaxb-xjc version " + JAXB_XJC_VERSION);

        ArtifactResolutionResult r = resolve(artifact);

        Stream<String> artifactEntry = Stream.of(artifact.getFile().getAbsolutePath());

        Stream<String> dependencyEntries = r.getArtifactResolutionNodes() //
                .stream() //
                .map(x -> x.getArtifact().getFile().getAbsolutePath());

        StringBuilder classpath = new StringBuilder();
        classpath.append( //
                Stream.concat(artifactEntry, dependencyEntries) //
                        .collect(Collectors.joining(File.pathSeparator)));

        ////////////////////////////////////////////////////////
        //
        // now grab the classpath entry for xjc-maven-plugin-core
        //
        ////////////////////////////////////////////////////////

        final URLClassLoader classLoader = (URLClassLoader) Thread.currentThread()
                .getContextClassLoader();

        for (final URL url : classLoader.getURLs()) {
            File file = new File(url.getFile());
            log.debug("plugin classpath entry: " + file.getAbsolutePath());
            // Note the contains check on xjc-maven-plugin-core because Travis runs mvn test
            // -B which gives us a classpath entry of xjc-maven-plugin-core/target/classes
            // (not a jar)
            if (file.getAbsolutePath().contains("xjc-maven-plugin-core")) {
                if (classpath.length() > 0) {
                    classpath.append(File.pathSeparator);
                }
                classpath.append(file.getAbsolutePath());
            }
        }
        log.debug("isolated classpath for call to xjc=\n  "
                + classpath.toString().replace(File.pathSeparator, File.pathSeparator + "\n  "));

        final String javaExecutable = System.getProperty("java.home") + File.separator + "bin"
                + File.separator + "java";
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

    private ArtifactResolutionResult resolve(Artifact artifact) {
        ArtifactResolutionRequest request = new ArtifactResolutionRequest() //
                .setArtifact(artifact) //
                .setLocalRepository(localRepository) //
                .setRemoteRepositories(remoteRepositories) //
                .setResolveTransitively(true);
        return repositorySystem.resolve(request);
    }

    private void ensureDestinationDirectoryExists() {
        for (int i = 0; i < arguments.size(); i++) {
            if (arguments.get(i).trim().equals("-d") && i < arguments.size() - 1) {
                File dir = new File(arguments.get(i + 1));
                if (!dir.exists()) {
                    getLog().info(
                            "destination directory (-d option) specified and does not exist, creating: "
                                    + dir);
                    dir.mkdirs();
                }
            }
        }
    }

}
