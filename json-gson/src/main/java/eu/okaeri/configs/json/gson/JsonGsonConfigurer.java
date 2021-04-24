package eu.okaeri.configs.json.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.postprocessor.ConfigPostprocessor;
import eu.okaeri.configs.schema.ConfigDeclaration;
import eu.okaeri.configs.schema.FieldDeclaration;
import eu.okaeri.configs.schema.GenericsDeclaration;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

public class JsonGsonConfigurer extends Configurer {

    private Map<String, Object> map;
    private Gson gson;

    public JsonGsonConfigurer() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        this.map = new LinkedHashMap<>();
    }

    public JsonGsonConfigurer(Gson gson) {
        this(gson, new LinkedHashMap<>());
    }

    public JsonGsonConfigurer(Gson gson, Map<String, Object> map) {
        this.gson = gson;
        this.map = map;
    }

    @Override
    public void setValue(String key, Object value, GenericsDeclaration type, FieldDeclaration field) {
        Object simplified = this.simplify(value, type, true);
        this.map.put(key, simplified);
    }

    @Override
    public Object getValue(String key) {
        return this.map.get(key);
    }

    @Override
    public boolean keyExists(String key) {
        return this.map.containsKey(key);
    }

    @Override
    public List<String> getAllKeys() {
        return Collections.unmodifiableList(new ArrayList<>(this.map.keySet()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load(InputStream inputStream, ConfigDeclaration declaration) throws Exception {

        String data = ConfigPostprocessor.of(inputStream).getContext();
        this.map = this.gson.fromJson(data, Map.class);

        if (this.map != null) {
            return;
        }

        this.map = new LinkedHashMap<>();
    }

    @Override
    public void write(OutputStream outputStream, ConfigDeclaration declaration) throws Exception {
        this.gson.toJson(this.map, new OutputStreamWriter(outputStream));
    }
}
