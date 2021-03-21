package test;

import eu.okaeri.configs.bukkit.BukkitConfigurer;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import test.impl.TestConfig;

import java.io.File;

public final class TestRunner {

    @SneakyThrows
    public static void main(String[] args) {

        File bindFile = new File("config.yml");
        BukkitConfigurer configurer = new BukkitConfigurer(new YamlConfiguration());

        TestConfig config = new TestConfig();
        config.setBindFile(bindFile);
        config.setConfigurer(configurer);

        config.save();
    }
}
