package tech.sebazcrc.permadeath;

import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import org.jetbrains.annotations.NotNull;

public class PermadeathLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        // No dependencies to load yet
    }
}
