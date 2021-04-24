package eu.okaeri.configs.json.simple;

import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.postprocessor.ConfigPostprocessor;
import eu.okaeri.configs.schema.ConfigDeclaration;
import eu.okaeri.configs.schema.FieldDeclaration;
import eu.okaeri.configs.schema.GenericsDeclaration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class JsonSimpleConfigurer extends Configurer {

    private Map<String, Object> map;
    private JSONParser parser;

    public JsonSimpleConfigurer() {
        this.parser = new JSONParser();
        this.map = new LinkedHashMap<>();
    }

    public JsonSimpleConfigurer(JSONParser parser) {
        this(parser, new LinkedHashMap<>());
    }

    public JsonSimpleConfigurer(JSONParser parser, Map<String, Object> map) {
        this.parser = parser;
        this.map = map;
    }

    @Override
    public Object simplify(Object value, GenericsDeclaration genericType, boolean conservative) throws OkaeriException {

        if (value == null) {
            return null;
        }

        GenericsDeclaration genericsDeclaration = GenericsDeclaration.of(value);
        if ((genericsDeclaration.getType() == char.class) || (genericsDeclaration.getType() == Character.class)) {
            return super.simplify(value, genericType, false);
        }

        return super.simplify(value, genericType, conservative);
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
        this.map = (Map<String, Object>) this.parser.parse(data);

        if (this.map != null) {
            return;
        }

        this.map = new LinkedHashMap<>();
    }

    @Override
    public void write(OutputStream outputStream, ConfigDeclaration declaration) throws Exception {
        JSONObject object = new JSONObject(this.map);
        ConfigPostprocessor.of(object.toJSONString()).write(outputStream);
    }
}
