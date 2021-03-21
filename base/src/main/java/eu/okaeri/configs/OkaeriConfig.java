package eu.okaeri.configs;

import eu.okaeri.configs.schema.ConfigDeclaration;
import eu.okaeri.configs.schema.FieldDeclaration;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;


public abstract class OkaeriConfig {

    @Getter @Setter private File bindFile;
    @Getter @Setter private Configurer configurer;
    private ConfigDeclaration declaration;

    public OkaeriConfig() {
        this.declaration = ConfigDeclaration.from(this);
    }

    public void save() throws IllegalAccessException, IOException {

        if (this.bindFile == null) {
            throw new IllegalAccessException("bindFile cannot be null");
        }

        if (this.configurer == null) {
            throw new IllegalAccessException("configurer cannot be null");
        }

        for (FieldDeclaration field : this.declaration.getFields()) {
            this.configurer.setValue(field.getName(), field.getValue());
        }

        this.configurer.writeToFile(this.bindFile, this.declaration);
    }

    public void load() throws IllegalAccessException, IOException {

        if (this.bindFile == null) {
            throw new IllegalAccessException("bindFile cannot be null");
        }

        if (this.configurer == null) {
            throw new IllegalAccessException("configurer cannot be null");
        }

        this.configurer.loadFromFile(this.bindFile, this.declaration);

        for (FieldDeclaration field : this.declaration.getFields()) {
            Object value = this.configurer.getValue(field.getName(), field.getType());
            field.updateValue(value);
        }
    }
}
