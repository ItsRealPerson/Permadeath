package dev.itsrealperson.permadeath;

import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

public class PermadeathLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        
        // MySQL Driver
        resolver.addDependency(new Dependency(new DefaultArtifact("com.mysql:mysql-connector-j:8.3.0"), null));
        
        // Jedis (Redis)
        resolver.addDependency(new Dependency(new DefaultArtifact("redis.clients:jedis:5.1.0"), null));
        
        // HikariCP
        resolver.addDependency(new Dependency(new DefaultArtifact("com.zaxxer:HikariCP:5.1.0"), null));

        // JDA (Discord) - Light version without opus
        resolver.addDependency(new Dependency(new DefaultArtifact("net.dv8tion:JDA:5.2.1"), null));

        // PacketEvents (Spigot)
        resolver.addDependency(new Dependency(new DefaultArtifact("com.github.retrooper:packetevents-spigot:2.11.1"), null));

        // Usar el mirror oficial de Google Cloud recomendado por PaperMC para evitar el error de CDN
        resolver.addRepository(new RemoteRepository.Builder("central", "default", "https://maven-central.storage-download.googleapis.com/maven2/").build());
        
        // Repositorio para PacketEvents
        resolver.addRepository(new RemoteRepository.Builder("codemc", "default", "https://repo.codemc.io/repository/maven-releases/").build());
        
        classpathBuilder.addLibrary(resolver);
    }
}
