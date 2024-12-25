package io.papermc.testplugin;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.protocol.Tunnel;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipFile;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.function.Predicate.not;

public final class TestPlugin extends JavaPlugin implements Listener {

    private static final Logger log = LoggerFactory.getLogger(TestPlugin.class);

    private final PackContainer resourcePackContainer = new PackContainer();
    private final PackContainer dataPackContainer = new PackContainer();
    private String ngrokUrl;

    private HttpServer server;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        try {
            Files.createDirectories(this.getDataFolder().toPath());
        } catch (IOException e) {
            log.error("Failed to create data folder", e);
        }

        try (var files = Files.list(this.getDataFolder().toPath())) {
            files
                    .filter(not(Files::isDirectory))
                    .filter(path -> path.getFileName().endsWith(".zip"))
                    .forEach(path -> {
                        try (ZipFile zipFile = new ZipFile(path.toFile());) {
                            var yamlFile = zipFile.getEntry("weeper-pack.yml");
                            if (yamlFile == null) {
                                return;
                            }
                            InputStream is = zipFile.getInputStream(yamlFile);

                            YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(is));

                            if (config.getString("name") == null || config.getString("id") == null) {
                                getLogger().warning("Invalid pack file: " + path.getFileName());
                                return;
                            }

                            log.info("Loading pack: {}, {}", config.getString("name"), config.getString("id"));

                            String dataPackPath = config.getString("paths.data-pack");
                            if (dataPackPath != null) {
                                copyInnerZipsToDataFolder(zipFile, dataPackPath).ifPresent(this.dataPackContainer::addPack);
                            }

                            String resourcePackPath = config.getString("paths.resource-pack");
                            if (resourcePackPath != null) {
                                copyInnerZipsToDataFolder(zipFile, resourcePackPath).ifPresent(this.resourcePackContainer::addPack);
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        getLogger().info("Found file: " + path.getFileName());
                    });
        } catch (IOException e) {
            log.error("Failed to list files", e);
        }

        InetSocketAddress address = new InetSocketAddress(80);
        Path path = this.getDataFolder().toPath();
        this.server = SimpleFileServer.createFileServer(address, path, SimpleFileServer.OutputLevel.VERBOSE);
        asyncExecutor().execute(server::start);


        final NgrokClient ngrokClient = new NgrokClient.Builder().build();
        final Tunnel httpTunnel = ngrokClient.connect();
        this.ngrokUrl = httpTunnel.getPublicUrl();
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        List<ResourcePackInfo> packInfos = resourcePackContainer.getPacks().stream().map(this::getPublicResourcePackUrl).toList();

        var request = ResourcePackRequest.resourcePackRequest().packs(packInfos).build();

        event.getPlayer().sendResourcePacks(request);
    }

    private ResourcePackInfo getPublicResourcePackUrl(Path zipFileName) {
        var fullUrl = URI.create(ngrokUrl + "/" + zipFileName.toString());
        return ResourcePackInfo.resourcePackInfo().id(UUID.randomUUID()).uri(fullUrl).asResourcePackInfo();
    }


    @Override
    public void onDisable() {
        if (server != null) {
            server.stop(30);
        }
    }

    private Optional<Path> copyInnerZipsToDataFolder(ZipFile file, String fileName) throws IOException {
        var entry = file.getEntry(fileName);

        if (entry == null) {
            log.warn("Could not find entry: {}, it is configured but not present.", fileName);
            return Optional.empty();
        }

        var destinationPath = this.getDataFolder().toPath().resolve(Path.of(fileName));
        Files.copy(
                file.getInputStream(entry),
                destinationPath,
                REPLACE_EXISTING
        );
        return Optional.of(destinationPath);
    }
}
