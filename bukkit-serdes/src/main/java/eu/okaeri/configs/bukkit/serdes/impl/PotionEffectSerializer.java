package eu.okaeri.configs.bukkit.serdes.impl;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectSerializer implements ObjectSerializer<PotionEffect> {

    @Override
    public Class<? super PotionEffect> getType() {
        return PotionEffect.class;
    }

    @Override
    public void serialize(PotionEffect potionEffect, SerializationData data) {
        data.add("amplifier", potionEffect.getAmplifier());
        data.add("duration", potionEffect.getDuration());
        data.add("type", potionEffect.getType());
    }

    @Override
    public PotionEffect deserialize(DeserializationData data, GenericsDeclaration generics) {

        int amplifier = data.get("amplifier", Integer.class);
        int duration = data.get("duration", Byte.class);
        PotionEffectType potionEffectType = data.get("type", PotionEffectType.class);

        return new PotionEffect(potionEffectType, duration, amplifier);
    }
}