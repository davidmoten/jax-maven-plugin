package com.github.davidmoten.jaxws;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Properties;
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
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;

class Util {
    
    private static final String PLUGIN_DESCRIPTOR = "pluginDescriptor";

    static File createOutputDirectoryIfSpecifiedOrDefault(Log log, String param, List<String> arguments) {
        for (int i = 0; i < arguments.size(); i++) {
            if (isOptionParamSpecifiedAndNotEmpty(arguments, i, param)) {
                File outputDir = new File(arguments.get(i + 1));
                if (!outputDir.exists()) {
                    log.info("destination directory (" + param + " option) specified and does not exist, creating: "
                            + outputDir);
                    outputDir.mkdirs();
                } 
                return outputDir;
            }
        }
        log.warn("destination directory (" + param
                + " option) NOT specified. Generated source will be placed in project root if -keep argument is present.");
        return new File(".");
    }

    private static boolean isOptionParamSpecifiedAndNotEmpty(List<String> arguments, int index, String param) {
        final String argValue = defaultIfBlank(arguments.get(index), EMPTY).trim();
        return StringUtils.equals(argValue, param) && index < arguments.size() - 1;
    }

    static ResolutionListener createLoggingResolutionListener(Log log) {
        return new ResolutionListener() {

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
                log("restrictRange: " + string(artifact) + ", replacement=" + string(replacement) + ", versionRange="
                        + newRange);
            }
        };
    }

    private static String spaces(int n) {
        StringBuilder b = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            b.append("  ");
        }
        return b.toString();
    }

    private static String string(Artifact a) {
        return a.getGroupId() + ":" + a.getArtifactId() + ":" + a.getVersion() + ":" + a.getScope() + ":" + a.getType();
    }

    static ArtifactResolutionResult resolve(Log log, Artifact artifact, RepositorySystem repositorySystem,
            ArtifactRepository localRepository, List<ArtifactRepository> remoteRepositories) {
        ArtifactResolutionRequest request = new ArtifactResolutionRequest() //
                .setArtifact(artifact) //
                .setLocalRepository(localRepository) //
                .setRemoteRepositories(remoteRepositories) //
                .setResolveTransitively(true) //
                .addListener(Util.createLoggingResolutionListener(log));
        return repositorySystem.resolve(request);
    }

    static String readConfigurationValue(String key) {
        Properties p = new Properties();
        try {
            p.load(WsImportMojo.class.getResourceAsStream("/configuration.properties"));
            return p.getProperty(key);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    static Stream<String> getPluginRuntimeDependencyEntries(AbstractMojo mojo, MavenProject project, Log log,
            RepositorySystem repositorySystem, ArtifactRepository localRepository,
            List<ArtifactRepository> remoteRepositories) {
        PluginDescriptor pluginDescriptor = (PluginDescriptor) mojo.getPluginContext().get(PLUGIN_DESCRIPTOR);
        Plugin plugin = project.getBuild().getPluginsAsMap().get(pluginDescriptor.getPluginLookupKey());

        List<ArtifactResolutionResult> artifactResolutionResults = plugin //
                .getDependencies() //
                .stream() //
                .map(repositorySystem::createDependencyArtifact) //
                .map(a -> Util.resolve(log, a, repositorySystem, localRepository, remoteRepositories)) //
                .collect(Collectors.toList());

        Stream<Artifact> originalArtifacts = artifactResolutionResults.stream()
                .map(ArtifactResolutionResult::getOriginatingArtifact);

        Stream<Artifact> childArtifacts = artifactResolutionResults.stream()
                .flatMap(resolutionResult -> resolutionResult.getArtifactResolutionNodes().stream())
                .map(ResolutionNode::getArtifact);

        return Stream.concat(originalArtifacts, childArtifacts).map(Artifact::getFile).map(File::getAbsolutePath);
    }

}
