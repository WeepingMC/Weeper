package io.papermc.testplugin;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
    public static final String RESOURCE_PACKS = "resource-packs";
    public static final String DATA_PACKS = "data-packs";

    private final PackContainer resourcePackContainer = new PackContainer();
    private final PackContainer dataPackContainer = new PackContainer();

    private ResourcePackRequest resourcePackRequest;

    private String ngrokUrl;
    private HttpServer server;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        try {
            Files.createDirectories(this.getDataPath());
            Files.createDirectories(this.getDataPath().resolve(RESOURCE_PACKS)).toFile().deleteOnExit();
            Files.createDirectories(this.getDataPath().resolve(DATA_PACKS)).toFile().deleteOnExit();
        } catch (IOException e) {
            log.error("Failed to create data folder", e);
        }

        try (var files = Files.list(this.getDataPath())) {
            files
                    .filter(not(Files::isDirectory))
                    .filter(path -> path.toString().endsWith(".jar"))
                    .peek(path -> log.info("Found jar file: {}", path.getFileName()))
                    .forEach(path -> {
                        try (ZipFile zipFile = new ZipFile(path.toFile());) {
                            var yamlFile = zipFile.getEntry("weeper-pack.yml");
                            if (yamlFile == null) {
                                log.error("Invalid pack file: {}", path.getFileName());
                                return;
                            }
                            try (InputStream is = zipFile.getInputStream(yamlFile); var reader = new InputStreamReader(is)) {
                                YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);

                                if (config.getString("name") == null || config.getString("id") == null) {
                                    log.warn("Invalid pack file: {}", path.getFileName());
                                    return;
                                }

                                log.info("Loading pack: {}, {}", config.getString("name"), config.getString("id"));

                                String dataPackPath = config.getString("paths.data-pack");
                                if (dataPackPath != null) {
                                    log.info("Loading data pack: {}", dataPackPath);
                                    copyInnerZipsToDataFolder(zipFile, dataPackPath, this.getDataPath().resolve(DATA_PACKS)).ifPresent(this.dataPackContainer::addPack);
                                }

                                String resourcePackPath = config.getString("paths.resource-pack");
                                if (resourcePackPath != null) {
                                    log.info("Loading resource pack: {}", resourcePackPath);
                                    copyInnerZipsToDataFolder(zipFile, resourcePackPath, this.getDataPath().resolve(RESOURCE_PACKS)).ifPresent(this.resourcePackContainer::addPack);
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        getLogger().info("Found file: " + path.getFileName());
                    });
        } catch (IOException e) {
            log.error("Failed to list files", e);
        }

        startServer();

        List<CompletableFuture<ResourcePackInfo>> packInfos = resourcePackContainer.getPacks().stream().map(this::getPublicResourcePackUrl).toList();

        CompletableFuture.allOf(packInfos.toArray(CompletableFuture[]::new)).whenComplete((unused, throwable) -> {

            List<ResourcePackInfo> list = new ArrayList<>();
            for (CompletableFuture<ResourcePackInfo> packInfo : packInfos) {
                ResourcePackInfo info;
                try {
                    info = packInfo.get();
                    list.add(info);
                } catch (InterruptedException | ExecutionException e) {
                    log.error("Failed to get pack info", e);
                }
            }

            resourcePackRequest = ResourcePackRequest.resourcePackRequest().packs(list).build();
            log.info("FInished building resource pack request");
        });

    }

    private void startServer() {
        InetSocketAddress address = new InetSocketAddress(80);
        Path path = this.getDataFolder().toPath().resolve(RESOURCE_PACKS).toAbsolutePath();
        this.server = SimpleFileServer.createFileServer(address, path, SimpleFileServer.OutputLevel.VERBOSE);
        asyncExecutor().execute(server::start);
        ngrokUrl = "http://" + address.getHostString();
        log.warn("Ngrok URL: {}", ngrokUrl);
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendResourcePacks(resourcePackRequest);
    }

    private CompletableFuture<ResourcePackInfo> getPublicResourcePackUrl(Path zipFileName) {
        var urlString = ngrokUrl + "/" + zipFileName.toString();
        log.warn("URL: {}", urlString);
        var fullUrl = URI.create(urlString);
        return ResourcePackInfo.resourcePackInfo().id(UUID.randomUUID()).uri(fullUrl).computeHashAndBuild();
    }


    @Override
    public void onDisable() {
        if (server != null) {
            server.stop(1);
        }
    }

    private Optional<Path> copyInnerZipsToDataFolder(ZipFile file, String fileName, Path destinationFolder) throws IOException {
        var entry = file.getEntry(fileName);

        if (entry == null) {
            log.warn("Could not find entry: {}, it is configured but not present.", fileName);
            return Optional.empty();
        }

        var destinationPath = destinationFolder.resolve(Path.of(fileName));
        Files.copy(
                file.getInputStream(entry),
                destinationPath,
                REPLACE_EXISTING
        );
        destinationPath.toFile().deleteOnExit();
        return Optional.of(Path.of(fileName));
    }
}
