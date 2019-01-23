package com.github.davidmoten.xjc;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ResolutionListener;
import org.apache.maven.artifact.resolver.ResolutionNode;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.sonatype.plexus.build.incremental.BuildContext;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;

import com.google.common.collect.Lists;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Mojo(name = "xjc")
public final class XjcMojo extends AbstractMojo {

    public static final String PLUGIN_DESCRIPTOR = "pluginDescriptor";

    @Parameter(required = true, name = "arguments")
    private List<String> arguments;

    @Parameter(name = "systemProperties")
    private Map<String, String> systemProperties;

    @Component
    private MavenProject mavenProject;

    @Component
    private BuildContext buildContext;

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

        File outputDir = createOutputDirectoryIfSpecifiedOrDefault();

        List<String> command = createCommand();

        try {
            new ProcessExecutor() //
                    .command(command) //
                    .exitValueNormal() //
                    .redirectOutput(System.out) //
                    .redirectError(System.out) //
                    .execute();

            buildContext.refresh(outputDir);
        } catch (InvalidExitValueException | IOException | InterruptedException | TimeoutException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        log.info("xjc mojo finished");
    }

    private List<String> createCommand() {

        // https://stackoverflow.com/questions/1440224/how-can-i-download-maven-artifacts-within-a-plugin

        Log log = getLog();

        String jaxbVersion = readJaxbVersion();

        ////////////////////////////////////////////////////////
        //
        // get the classpath entries for the deps of jaxb-xjc
        //
        ////////////////////////////////////////////////////////

        Artifact artifact = repositorySystem.createArtifact( //
                "org.glassfish.jaxb", "jaxb-xjc", jaxbVersion, "", "jar");

        log.info("setting up classpath for jaxb-xjc version " + jaxbVersion);

        ArtifactResolutionResult r = resolve(artifact);

        Stream<String> artifactEntry = Stream.of(artifact.getFile().getAbsolutePath());

        Stream<String> dependencyEntries = r.getArtifactResolutionNodes() //
                .stream() //
                .map(x -> x.getArtifact().getFile().getAbsolutePath());

        Stream<String> fullDependencyEntries = Stream.concat(dependencyEntries, getPluginRuntimeDependencyEntries());

        StringBuilder classpath = new StringBuilder();
        classpath.append( //
                Stream.concat(artifactEntry, fullDependencyEntries) //
                        .collect(Collectors.joining(File.pathSeparator)));
        ////////////////////////////////////////////////////////
        //
        // now grab the classpath entry for xjc-maven-plugin-core
        //
        ////////////////////////////////////////////////////////
        final URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();

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

    private Stream<String> getPluginRuntimeDependencyEntries()
    {
        PluginDescriptor pluginDescriptor = (PluginDescriptor) getPluginContext().get(PLUGIN_DESCRIPTOR);
        Plugin plugin = mavenProject.getBuild().getPluginsAsMap().get(pluginDescriptor.getPluginLookupKey());

        List<ArtifactResolutionResult> artifactResolutionResults = plugin.getDependencies().stream()
            .map(repositorySystem::createDependencyArtifact)
            .map(this::resolve)
            .collect(Collectors.toList());

        Stream<Artifact> originalArtifacts = artifactResolutionResults.stream()
            .map(ArtifactResolutionResult::getOriginatingArtifact);

        Stream<Artifact> childArtifacts = artifactResolutionResults.stream()
            .flatMap(resolutionResult -> resolutionResult.getArtifactResolutionNodes().stream())
            .map(ResolutionNode::getArtifact);

        return Stream.concat(originalArtifacts, childArtifacts)
            .map(Artifact::getFile)
            .map(File::getAbsolutePath);
    }

    private static String readJaxbVersion() {
        Properties p = new Properties();
        try {
            p.load(XjcMojo.class.getResourceAsStream("/configuration.properties"));
            return p.getProperty("glassfish.jaxb.version");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String spaces(int n) {
        StringBuilder b = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            b.append("  ");
        }
        return b.toString();
    }

    private ArtifactResolutionResult resolve(Artifact artifact) {
        Log log = getLog();
        ArtifactResolutionRequest request = new ArtifactResolutionRequest() //
                .setArtifact(artifact) //
                .setLocalRepository(localRepository) //
                .setRemoteRepositories(remoteRepositories) //
                .setResolveTransitively(true) //
                .addListener(new ResolutionListener() {

                    int depth = 0;

                    private void log(String message) {
                        log.debug(spaces(depth) + message);
                    }

                    @Override
                    public void testArtifact(Artifact artifact) {
                        log("testArtifact: " + artifact.getArtifactId());
                    }

                    @Override
                    public void startProcessChildren(Artifact artifact) {
                        log("startProcessChildren: " + string(artifact));
                        depth++;
                    }

                    @Override
                    public void endProcessChildren(Artifact artifact) {
                        depth--;
                        log("endProcessChildren: " + string(artifact));
                    }

                    @Override
                    public void includeArtifact(Artifact artifact) {
                        log("includeArtifact: " + string(artifact));
                    }

                    @Override
                    public void omitForNearer(Artifact omitted, Artifact kept) {
                        log("omitForNearer: omitted=" + string(omitted) + ", kept=" + string(kept));
                    }

                    @Override
                    public void updateScope(Artifact artifact, String scope) {
                        log("updateScope: " + string(artifact) + ", scope=" + scope);
                    }

                    @Override
                    public void manageArtifact(Artifact artifact, Artifact replacement) {
                        log("manageArtifact: " + string(artifact) + ", replacement=" + string(replacement));
                    }

                    @Override
                    public void omitForCycle(Artifact artifact) {
                        log("omitForCycle: " + string(artifact));
                    }

                    @Override
                    public void updateScopeCurrentPom(Artifact artifact, String ignoredScope) {
                        log("updateScopeCurrentPom: " + string(artifact));
                    }

                    @Override
                    public void selectVersionFromRange(Artifact artifact) {
                        log("selectVersionFromRange: " + string(artifact));
                    }

                    @Override
                    public void restrictRange(Artifact artifact, Artifact replacement, VersionRange newRange) {
                        log("restrictRange: " + string(artifact) + ", replacement=" + string(replacement)
                                + ", versionRange=" + newRange);
                    }
                });
        return repositorySystem.resolve(request);
    }

    private static String string(Artifact a) {
        return a.getGroupId() + ":" + a.getArtifactId() + ":" + a.getVersion() + ":" + a.getScope() + ":" + a.getType();
    }

    private File createOutputDirectoryIfSpecifiedOrDefault() {
        for (int i = 0; i < arguments.size(); i++) {
            if (isDirParamSpecifiedAndNotEmpty(arguments, i)) {
                File outputDir = new File(arguments.get(i + 1));
                if (!outputDir.exists()) {
                    getLog().info("destination directory (-d option) specified and does not exist, creating: " + outputDir);
                    outputDir.mkdirs();
                    return outputDir;
                }
            }
        }
        getLog().warn("destination directory (-d option) NOT specified. Generated source will be placed in project root.");
        return new File(".");
    }

    private boolean isDirParamSpecifiedAndNotEmpty(List<String> arguments, int index) {
        final String argValue = defaultIfBlank(arguments.get(index), EMPTY).trim();
        return StringUtils.equals(argValue, "-d") && index < arguments.size() - 1;
    }

}
