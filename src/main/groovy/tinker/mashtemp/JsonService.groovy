package tinker.mashtemp

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer

/**
 * Marshaling to/from JSON using Jackson.
 */
class JsonService {

    private final ObjectMapper mapper = new ObjectMapper()

    JsonService() {
        SimpleModule module = new SimpleModule("Pork")
        module.addSerializer(GString.class, ToStringSerializer.instance);
        mapper.registerModule(module)

//        mapper.configure(SerializationFeature.WRITE_NULL_PROPERTIES, false)
        mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false)
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    String toJson(Object o) { mapper.writeValueAsString(o) }

    public <T> T fromJson(String content, Class<T> klass) { mapper.readValue(content, klass) }

}
