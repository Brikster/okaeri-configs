package eu.okaeri.configs.schema;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Header;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class ConfigDeclaration {

    public static ConfigDeclaration from(OkaeriConfig config) {

        ConfigDeclaration declaration = new ConfigDeclaration();
        Class<? extends OkaeriConfig> clazz = config.getClass();

        Header header = clazz.getAnnotation(Header.class);
        if (header != null) {
            declaration.setHeader(header.value());
        }

        declaration.setFields(Arrays.stream(clazz.getDeclaredFields())
                .map(field -> FieldDeclaration.from(field, config))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        return declaration;
    }

    public Optional<FieldDeclaration> getField(String key) {
        return this.fields.stream()
                .filter(field -> field.getName().equals(key))
                .findAny();
    }

    public GenericsDeclaration getFieldDeclarationOrNull(String key) {

        Optional<FieldDeclaration> genericField = this.getField(key);
        GenericsDeclaration genericType = null;

        if (genericField.isPresent()) {
            FieldDeclaration field = genericField.get();
            genericType = field.getType();
        }

        return genericType;
    }

    private String[] header;
    private List<FieldDeclaration> fields;
}
