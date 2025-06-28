package cn.zpaas.lowcode.utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.JavaVersion;
import com.google.gson.internal.PreJava9DateFormatProvider;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;

/**
 * Json常用操作封闭工具类
 *
 * @author zjy
 * createTime 2025年04月22日-10:20:18
 */
public class JsonUtils {
    private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    public static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setPrettyPrinting()
            .serializeNulls()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .registerTypeAdapterFactory(DateNullTypeAdapter.FACTORY)
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    public static final Configuration JSON_PATH_CFG = Configuration.builder()
            .options(Option.DEFAULT_PATH_LEAF_TO_NULL)
            .jsonProvider(new GsonJsonProvider(GSON))
            .mappingProvider(new GsonMappingProvider(GSON))
            .build();

    private JsonUtils() {

    }

    /**
     * 将对象转换为Json格式
     * 
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        if (object == null) {
            return StringUtils.emptyString();
        }
        try {
            return GSON.toJson(object);
        } catch (Exception e) {
            logger.error("transfer to JSON String failed!", e);
        }
        return null;
    }

    /**
     * 将Json字符串转换为对象
     * 
     * @param <T>
     * @param jsonString
     * @param clazz
     * @return
     */
    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            return GSON.fromJson(jsonString, clazz);
        } catch (Exception e) {
            logger.error("transfer to Object failed!", e);
        }
        return null;
    }

    /**
     * 将json字符串转换为JsonObject对象
     * 
     * @param jsonString
     * @return
     */
    public static JsonObject toJsonObject(String jsonString) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            return GSON.fromJson(jsonString, JsonObject.class);
        } catch (Exception e) {
            logger.error("transfer to JsonObject failed! {}", jsonString, e);
        }
        return null;
    }

    /*
     * 将json字符串转换为JsonArray对象
     */
    public static JsonArray toJsonArray(String jsonString) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            return GSON.fromJson(jsonString, JsonArray.class);
        } catch (Exception e) {
            logger.error("transfer to JsonArray failed!", e);
        }
        return null;
    }

    /**
     * 将java对象换为JsonObject对象
     * 
     * @param jsonString
     * @return
     */
    public static JsonObject toJsonObject(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return GSON.toJsonTree(object).getAsJsonObject();
        } catch (Exception e) {
            logger.error("transfer to JsonObject failed!", e);
        }
        return null;
    }

    /**
     * 将列表对象转换为JsonArray对象
     * 
     * @param collection
     * @return
     */
    public static JsonArray toJsonArray(Collection<?> collection) {
        if (collection == null) {
            return null;
        }
        try {
            return GSON.toJsonTree(collection).getAsJsonArray();
        } catch (Exception e) {
            logger.error("transfer to JsonArray failed!", e);
        }
        return null;
    }

    /**
     * 将对象转换为JsonElement
     * 
     * @param object
     * @return
     */
    public static JsonElement toJsonElement(Object object) {
        if (object == null) {
            return null;
        }
        try {
            if (object instanceof JsonElement) {
                return (JsonElement) object;
            }
            return GSON.toJsonTree(object);
        } catch (Exception e) {
            logger.error("transfer to JsonElement failed!", e);
        }
        return null;
    }

    /**
     * 返回Object对象
     * 
     * @param jsonElement
     * @return
     */
    public static Object toObject(JsonElement jsonElement) {
        return toObject(jsonElement, false);
    }

    /**
     * 返回Object对象
     * 
     * @param jsonElement
     * @param isRecursion
     * @return
     */
    public static Object toObject(JsonElement jsonElement, boolean isRecursion) {
        if (jsonElement == null) {
            return null;
        }
        if (jsonElement.isJsonArray()) {
            if(isRecursion) {
                return toList(jsonElement.getAsJsonArray(), isRecursion);
            }else {
                return jsonElement.getAsJsonArray();
            }
        } else if (jsonElement.isJsonObject()) {
            if(isRecursion) {
                return toMap(jsonElement.getAsJsonObject(), isRecursion);
            }else {
                return jsonElement.getAsJsonObject();
            }
        } else if (jsonElement.isJsonNull()) {
            return null;
        } else {
            JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
            if (jsonPrimitive.isBoolean()) {
                return jsonPrimitive.getAsBoolean();
            } else if (jsonPrimitive.isNumber()) {
                return unwrapNumber(jsonPrimitive.getAsNumber());
            } else if (jsonPrimitive.isString()) {
                return jsonPrimitive.getAsString();
            } else {
                return null;
            }
        }
    }

    /**
     * 是否原始数据类型
     * 
     * @param n
     * @return
     */
    private static boolean isPrimitiveNumber(final Number n) {
        return n instanceof Integer ||
                n instanceof Float ||
                n instanceof Double ||
                n instanceof Long ||
                n instanceof BigDecimal ||
                n instanceof BigInteger;
    }

    /**
     * 转换成原始数据类型
     * 
     * @param n
     * @return
     */
    public static Number unwrapNumber(final Number n) {
        Number unwrapped;

        if (!isPrimitiveNumber(n)) {
            BigDecimal bigDecimal = new BigDecimal(n.toString());
            if (bigDecimal.scale() <= 0) {
                if (bigDecimal.abs().compareTo(new BigDecimal(Integer.MAX_VALUE)) <= 0) {
                    unwrapped = bigDecimal.intValue();
                } else if (bigDecimal.abs().compareTo(new BigDecimal(Long.MAX_VALUE)) <= 0) {
                    unwrapped = bigDecimal.longValue();
                } else {
                    unwrapped = bigDecimal;
                }
            } else {
                final double doubleValue = bigDecimal.doubleValue();
                if (BigDecimal.valueOf(doubleValue).compareTo(bigDecimal) != 0) {
                    unwrapped = bigDecimal;
                } else {
                    unwrapped = doubleValue;
                }
            }
        } else {
            unwrapped = n;
        }
        return unwrapped;
    }

    /**
     * 获取Object类型的属性值
     * 
     * @param jsonObject
     * @param key
     * @return
     */
    public static Object getObject(JsonObject jsonObject, String key) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return null;
        }
        if (StringUtils.isBlank(key) || !jsonObject.has(key) || jsonObject.get(key).isJsonNull()) {
            return null;
        }
        JsonElement jsonElement = jsonObject.get(key);
        return toObject(jsonElement);
    }

    /**
     * 获取字符串类型的属性值
     * 
     * @param jsonObject
     * @param key
     * @return
     */
    public static String getString(JsonObject jsonObject, String key) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return null;
        }
        if (StringUtils.isBlank(key) || !jsonObject.has(key) || jsonObject.get(key).isJsonNull()) {
            return null;
        }
        return jsonObject.get(key).getAsString();
    }

    /**
     * 获取boolean类型的属性值
     * 
     * @param jsonObject
     * @param key
     * @return
     */
    public static boolean getBoolean(JsonObject jsonObject, String key) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return false;
        }
        if (StringUtils.isBlank(key) || !jsonObject.has(key) || jsonObject.get(key).isJsonNull()) {
            return false;
        }
        return jsonObject.get(key).getAsBoolean();
    }

    /**
     * 获取Integer类型的属性值
     * 
     * @param jsonObject
     * @param key
     * @return
     */
    public static Integer getInteger(JsonObject jsonObject, String key) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return null;
        }
        if (StringUtils.isBlank(key) || !jsonObject.has(key) || jsonObject.get(key).isJsonNull()) {
            return null;
        }
        return jsonObject.get(key).getAsInt();
    }

    /**
     * 获取Float类型的属性值
     * 
     * @param jsonObject
     * @param key
     * @return
     */
    public static Float getFloat(JsonObject jsonObject, String key) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return null;
        }
        if (StringUtils.isBlank(key) || !jsonObject.has(key) || jsonObject.get(key).isJsonNull()) {
            return null;
        }
        return jsonObject.get(key).getAsFloat();
    }

    /**
     * 获取Double类型的属性值
     * 
     * @param jsonObject
     * @param key
     * @return
     */
    public static Double getDouble(JsonObject jsonObject, String key) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return null;
        }
        if (StringUtils.isBlank(key) || !jsonObject.has(key) || jsonObject.get(key).isJsonNull()) {
            return null;
        }
        return jsonObject.get(key).getAsDouble();
    }

    /**
     * 获取Long类型的属性值
     * 
     * @param jsonObject
     * @param key
     * @return
     */
    public static Long getLong(JsonObject jsonObject, String key) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return null;
        }
        if (StringUtils.isBlank(key) || !jsonObject.has(key) || jsonObject.get(key).isJsonNull()) {
            return null;
        }
        return jsonObject.get(key).getAsLong();
    }

    /**
     * 获取BigDecimal类型的属性值
     * 
     * @param jsonObject
     * @param key
     * @return
     */
    public static BigDecimal getBigDecimal(JsonObject jsonObject, String key) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return null;
        }
        if (StringUtils.isBlank(key) || !jsonObject.has(key) || jsonObject.get(key).isJsonNull()) {
            return null;
        }
        return jsonObject.get(key).getAsBigDecimal();
    }

    /**
     * 获取BigInteger类型的属性值
     * 
     * @param jsonObject
     * @param key
     * @return
     */
    public static BigInteger getBigInteger(JsonObject jsonObject, String key) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return null;
        }
        if (StringUtils.isBlank(key) || !jsonObject.has(key) || jsonObject.get(key).isJsonNull()) {
            return null;
        }
        return jsonObject.get(key).getAsBigInteger();
    }

    /**
     * 获取Byte类型的属性值
     * 
     * @param jsonObject
     * @param key
     * @return
     */
    public static Byte getByte(JsonObject jsonObject, String key) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return null;
        }
        if (StringUtils.isBlank(key) || !jsonObject.has(key) || jsonObject.get(key).isJsonNull()) {
            return null;
        }
        return jsonObject.get(key).getAsByte();
    }

    /**
     * 获取JsonArray类型的属性值
     * 
     * @param jsonObject
     * @param key
     * @return
     */
    public static JsonArray getJsonArray(JsonObject jsonObject, String key) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return null;
        }
        if (StringUtils.isBlank(key) || !jsonObject.has(key) || jsonObject.get(key).isJsonNull()) {
            return null;
        }
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement.isJsonArray()) {
            return jsonElement.getAsJsonArray();
        } else if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
            return toJsonArray(jsonElement.getAsString());
        } else {
            logger.error("the JsonElement is not type of JsonArray!");
            return null;
        }
    }

    /**
     * 获取JsonObject类型的属性值
     * 
     * @param jsonObject
     * @param key
     * @return
     */
    public static JsonObject getJsonObject(JsonObject jsonObject, String key) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return null;
        }
        if (StringUtils.isBlank(key) || !jsonObject.has(key) || jsonObject.get(key).isJsonNull()) {
            return null;
        }
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
        } else if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
            return toJsonObject(jsonElement.getAsString());
        } else {
            logger.error("the JsonElement is not type of JsonObject!");
            return null;
        }
    }

    /**
     * 将Json对象转换为Map对象
     * 
     * @param jsonObject
     * @return
     */
    public static Map<String, Object> toMap(JsonObject jsonObject) {
        return toMap(jsonObject, false);
    }

    /**
     * 将Json对象转换为Map对象
     * 
     * @param jsonObject
     * @param isRecursion
     * @return
     */
    public static Map<String, Object> toMap(JsonObject jsonObject, boolean isRecursion) {
        Map<String, Object> resultMap = new HashMap<>();
        if (jsonObject == null) {
            return resultMap;
        }
        jsonObject.asMap().forEach((k, v) -> {
            resultMap.put(k, JsonUtils.toObject(v, isRecursion));
        });
        return resultMap;
    }

    /**
     * 将Json数组转换为List
     * @param jsonArray
     * @return
     */
    public static List<Object> toList(JsonArray jsonArray) {
        return toList(jsonArray, false);
    }

    /**
     * 将Json数组转换为List
     * @param jsonArray
     * @param isRecursion
     * @return
     */
    public static List<Object> toList(JsonArray jsonArray, boolean isRecursion) {
        List<Object> result = new ArrayList<>();
        if (jsonArray== null) {
            return result;
        }
        jsonArray.asList().forEach((v) -> {
            result.add(JsonUtils.toObject(v, isRecursion));
        });
        return result;
    }

    /**
     * 根据jsonpath获取属性值
     * 
     * @param rootObject
     * @param path
     * @return
     */
    public static Object eval(Object rootObject, String path) {
        if (rootObject == null || StringUtils.isBlank(path)) {
            return null;
        }
        try {
            Object result = JsonPath.compile(path).read(rootObject, JSON_PATH_CFG);
            if (result == null) {
                return null;
            } else {
                if (result instanceof JsonElement) {
                    return toObject(((JsonElement) result));
                } else {
                    return result;
                }
            }
        } catch (Exception e) {
            logger.info("get property value by JsonPath failed!", e);
            return null;
        }
    }

    /**
     * 更新jsonpath指向属性的值
     * 
     * @param rootJson
     * @param path
     * @param newValue
     * @return
     */
    public static <T> T set(Object rootJson, String path, Object newValue) {
        if (rootJson == null || StringUtils.isBlank(path)) {
            return null;
        }
        try {
            return JsonPath.compile(path).set(rootJson, newValue, JSON_PATH_CFG);
        } catch (Exception e) {
            logger.info("using JsonPath to update property value failed!", e);
            return null;
        }
    }

    /**
     * 判断JsonArray是否为空
     * 
     * @param jsonArray
     * @return
     */
    public static boolean isEmpty(JsonArray jsonArray) {
        if (jsonArray == null || jsonArray.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断JsonObject是否为空
     * 
     * @param jsonObject
     * @return
     */
    public static boolean isEmpty(JsonObject jsonObject) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
}

class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public JsonElement serialize(LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(date.format(DATE_TIME_FORMATTER));
    }

    @Override
    public LocalDateTime deserialize(JsonElement element, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        String timestamp = element.getAsJsonPrimitive().getAsString();
        return LocalDateTime.parse(timestamp, DATE_TIME_FORMATTER);
    }
}

/**
 * Adapter for Date. Although this class appears stateless, it is not.
 * DateFormat captures its time zone and locale when it is created, which gives
 * this class state. DateFormat isn't thread safe either, so this class has
 * to synchronize its read and write methods.
 */
class DateNullTypeAdapter extends TypeAdapter<Date> {
    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @SuppressWarnings("unchecked") // we use a runtime check to make sure the 'T's equal
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            return typeToken.getRawType() == Date.class ? (TypeAdapter<T>) new DateNullTypeAdapter() : null;
        }
    };

    /**
     * List of 1 or more different date formats used for de-serialization attempts.
     * The first of them (default US format) is used for serialization as well.
     */
    private final List<DateFormat> dateFormats = new ArrayList<>();

    public DateNullTypeAdapter() {
        dateFormats.add(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US));
        if (!Locale.getDefault().equals(Locale.US)) {
            dateFormats.add(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT));
        }
        if (JavaVersion.isJava9OrLater()) {
            dateFormats.add(PreJava9DateFormatProvider.getUSDateTimeFormat(DateFormat.DEFAULT, DateFormat.DEFAULT));
        }
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return deserializeToDate(in);
    }

    private Date deserializeToDate(JsonReader in) throws IOException {
        String s = in.nextString();
        if (StringUtils.isBlank(s)) {
            return null;
        }
        synchronized (dateFormats) {
            for (DateFormat dateFormat : dateFormats) {
                try {
                    return dateFormat.parse(s);
                } catch (ParseException ignored) {
                }
            }
        }
        try {
            return ISO8601Utils.parse(s, new ParsePosition(0));
        } catch (ParseException e) {
            throw new JsonSyntaxException("Failed parsing '" + s + "' as Date; at path " + in.getPreviousPath(), e);
        }
    }

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        DateFormat dateFormat = dateFormats.get(0);
        String dateFormatAsString;
        synchronized (dateFormats) {
            dateFormatAsString = dateFormat.format(value);
        }
        out.value(dateFormatAsString);
    }
}
