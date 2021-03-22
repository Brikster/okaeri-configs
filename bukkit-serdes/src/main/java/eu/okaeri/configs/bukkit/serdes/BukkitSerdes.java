package eu.okaeri.configs.bukkit.serdes;

import eu.okaeri.configs.bukkit.serdes.impl.ItemMetaSerializer;
import eu.okaeri.configs.bukkit.serdes.impl.ItemStackSerializer;
import eu.okaeri.configs.bukkit.serdes.impl.LocationSerializer;
import eu.okaeri.configs.bukkit.serdes.impl.PotionEffectSerializer;
import eu.okaeri.configs.bukkit.serdes.transformer.StringEnchantmentTransformer;
import eu.okaeri.configs.bukkit.serdes.transformer.StringWorldTransformer;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.transformer.TransformerRegistry;

public class BukkitSerdes implements OkaeriSerdesPack {

    @Override
    public void register(TransformerRegistry registry) {

        // serializer/deserializer
        registry.register(new ItemMetaSerializer());
        registry.register(new ItemStackSerializer());
        registry.register(new LocationSerializer());
        registry.register(new PotionEffectSerializer());

        // transformers
        registry.register(new StringWorldTransformer());
        registry.register(new StringEnchantmentTransformer());
    }
}
