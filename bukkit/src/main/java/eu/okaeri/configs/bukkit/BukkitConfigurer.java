package eu.okaeri.configs.bukkit;

import eu.okaeri.configs.ConfigUtil;
import eu.okaeri.configs.Configurer;
import eu.okaeri.configs.schema.ConfigDeclaration;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import eu.okaeri.configs.transformer.TransformerRegistry;
import lombok.AllArgsConstructor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

@AllArgsConstructor
public class BukkitConfigurer extends Configurer {

    private YamlConfiguration config;

    @Override
    public String getCommentPrefix() {
        return "#";
    }

    @Override
    public String getSectionSeparator() {
        return "\n";
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setValue(String key, Object value) {

        ObjectSerializer serializer = TransformerRegistry.getSerializer(value.getClass());
        if (serializer == null) {
            this.config.set(key, value);
            return;
        }

        SerializationData serializationData = new SerializationData();
        serializer.serialize(value, serializationData);

        this.config.set(key, serializationData.asMap());
    }

    @Override
    public Object getValue(String key) {
        return this.config.get(key);
    }

    @Override
    public boolean keyExists(String key) {
        return this.config.getKeys(false).contains(key);
    }

    @Override
    public <T> T resolveType(Object object, Class<T> clazz, GenericsDeclaration type) {

        if ((object instanceof MemorySection) && (clazz == Map.class)) {

            Map<String, Object> values = ((MemorySection) object).getValues(false);
            GenericsDeclaration keyDeclaration = type.getSubtype().get(0);
            GenericsDeclaration valueDeclaration = type.getSubtype().get(1);
            Map<Object, Object> map = new LinkedHashMap<>();

            for (Map.Entry<String, Object> entry : values.entrySet()) {
                Object key = this.resolveType(entry.getKey(), keyDeclaration.getType(), keyDeclaration);
                Object value = this.resolveType(entry.getValue(), valueDeclaration.getType(), valueDeclaration);
                map.put(key, value);
            }

            return super.resolveType(map, clazz, type);
        }

        if (object instanceof MemorySection) {

            ObjectSerializer serializer = TransformerRegistry.getSerializer(clazz);
            if (serializer == null) {
                return super.resolveType(object, clazz, null);
            }

            Map<String, Object> values = ((MemorySection) object).getValues(false);
            return clazz.cast(serializer.deserialize(new DeserializationData(values), type));
        }

        return super.resolveType(object, clazz, null);
    }

    @Override
    public void loadFromFile(File file, ConfigDeclaration declaration) throws IOException {
        try {
            this.config.load(file);
        } catch (InvalidConfigurationException exception) {
            throw new IOException(exception);
        }
    }

    @Override
    public void writeToFile(File file, ConfigDeclaration declaration) throws IOException {

        this.config.save(file);

        String data = this.readFile(file);
        data = ConfigUtil.removeStartingWith(this.getCommentPrefix(), data);
        data = ConfigUtil.addCommentsToFields(this.getCommentPrefix(), this.getSectionSeparator(), data, declaration);

        String header = ConfigUtil.convertToComment(this.getCommentPrefix(), declaration.getHeader(), true);
        String output = "";

        if (header != null) {
            output += header;
            output += this.getSectionSeparator();
        }

        output += data;
        this.writeFile(file, output);
    }

    private String readFile(File file) throws IOException {
        StringBuilder fileContents = new StringBuilder((int) file.length());
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine()).append("\n");
            }
            return fileContents.toString();
        }
    }

    private void writeFile(File file, String text) throws FileNotFoundException {
        try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
            out.print(text);
        }
    }
}