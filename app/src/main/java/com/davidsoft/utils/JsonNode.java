package com.davidsoft.utils;

import java.lang.reflect.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class JsonNode {

    public static final int TYPE_PLAIN = 0;
    public static final int TYPE_OBJECT = 1;
    public static final int TYPE_ARRAY = 2;

    private final int type;
    private final LinkedHashMap<String, JsonNode> fields;
    private final JsonNode[] items;
    private String value;
    private boolean simple;

    public static final JsonNode NULL = new JsonNode("null", true);

    public static JsonNode createEmptyObject() {
        return new JsonNode(new LinkedHashMap<>());
    }

    public JsonNode(Map<String, JsonNode> fields) {
        this.type = TYPE_OBJECT;
        this.simple = false;
        if (fields instanceof LinkedHashMap) {
            this.fields = (LinkedHashMap<String, JsonNode>) fields;
        }
        else {
            this.fields = new LinkedHashMap<>(fields);
        }
        this.items = null;
        this.value = null;
    }

    public JsonNode(JsonNode[] items) {
        this.type = TYPE_ARRAY;
        this.simple = false;
        this.fields = null;
        this.items = items;
        this.value = null;
    }

    public JsonNode(Collection<JsonNode> items) {
        this.type = TYPE_ARRAY;
        this.simple = false;
        this.fields = null;
        this.items = new JsonNode[items.size()];
        items.toArray(this.items);
        this.value = null;
    }

    public JsonNode(String value, boolean simple) {
        this.type = TYPE_PLAIN;
        this.simple = simple;
        this.fields = null;
        this.items = null;
        this.value = value;
    }

    public boolean isObject() {
        return type == TYPE_OBJECT;
    }

    public boolean isArray() {
        return type == TYPE_ARRAY;
    }

    public boolean isPlain() {
        return type == TYPE_PLAIN;
    }

    public boolean isNull() {
        return type == TYPE_PLAIN && simple && "null".equals(value);
    }

    public int getType() {
        return type;
    }

    public boolean isSimple() {
        return simple;
    }

    public JsonNode getField(String fieldName) {
        if (type == TYPE_OBJECT) {
            return fields.get(fieldName);
        }
        throw new IllegalStateException("此Json节点不是对象。");
    }

    public void setField(String fieldName, Object value) {
        if (type == TYPE_OBJECT) {
            fields.put(fieldName, valueOf(value));
            return;
        }
        throw new IllegalStateException("此Json节点不是对象。");
    }

    public int getLength() {
        if (type == TYPE_ARRAY) {
            return items.length;
        }
        throw new IllegalStateException("此Json节点不是数组。");
    }

    public JsonNode getItem(int position) {
        if (type == TYPE_ARRAY) {
            return items[position];
        }
        throw new IllegalStateException("此Json节点不是数组。");
    }

    public void setItem(int position, JsonNode value) {
        if (type == TYPE_ARRAY) {
            items[position] = value;
            return;
        }
        throw new IllegalStateException("此Json节点不是数组。");
    }

    public String getValue() {
        if (type == TYPE_PLAIN) {
            return value;
        }
        throw new IllegalStateException("此Json节点不是数值。");
    }

    public void setValue(String value, boolean simple) {
        if (type == TYPE_PLAIN) {
            this.value = value;
            this.simple = simple;
            return;
        }
        throw new IllegalStateException("此Json节点不是数值。");
    }

    //与Java对象互转

    public static JsonNode valueOf(Object o) {
        if (o == null) {
            return new JsonNode("null", true);
        }
        Class<?> c = o.getClass();
        if (o instanceof JsonNode) {
            return (JsonNode) o;
        }
        else if (c.isArray()) {
            JsonNode[] nodes = new JsonNode[Array.getLength(o)];
            for (int i = 0; i < nodes.length; i++) {
                nodes[i] = valueOf(Array.get(o, i));
            }
            return new JsonNode(nodes);
        }
        else if (o instanceof Boolean) {
            return new JsonNode(String.valueOf(o), true);
        }
        else if (o instanceof Byte) {
            return new JsonNode(String.valueOf(o), true);
        }
        else if (o instanceof Character) {
            return new JsonNode(String.valueOf(o), false);
        }
        else if (o instanceof Short) {
            return new JsonNode(String.valueOf(o), true);
        }
        else if (o instanceof Integer) {
            return new JsonNode(String.valueOf(o), true);
        }
        else if (o instanceof Long) {
            return new JsonNode(String.valueOf(o), true);
        }
        else if (o instanceof Float) {
            return new JsonNode(String.valueOf(o), true);
        }
        else if (o instanceof Double) {
            return new JsonNode(String.valueOf(o), true);
        }
        else if (o instanceof String) {
            return new JsonNode((String) o, false);
        }
        else if (o instanceof Collection) {
            Collection<?> collection = (Collection<?>) o;
            JsonNode[] nodes = new JsonNode[collection.size()];
            int i = 0;
            for (Object component : collection) {
                nodes[i++] = valueOf(component);
            }
            return new JsonNode(nodes);
        }
        else {
            LinkedHashMap<String, JsonNode> jsonFields = new LinkedHashMap<>();
            for (Field field : c.getFields()) {
                try {
                    if (field.getType().isPrimitive()) {
                        jsonFields.put(field.getName(), new JsonNode(String.valueOf(field.get(o)), true));
                    }
                    else {
                        jsonFields.put(field.getName(), valueOf(field.get(o)));
                    }
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return new JsonNode(jsonFields);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T toObject(JsonNode jsonNode, Class<T> destClass) {
        if (destClass.equals(JsonNode.class)) {
            return (T) jsonNode;
        }
        else if (destClass.isArray()) {
            if (jsonNode.isNull()) {
                return null;
            }
            if (!jsonNode.isArray()) {
                throw new IllegalStateException("jsonNode was not an array.");
            }
            int arrayLength = jsonNode.getLength();
            Class<?> c = destClass.getComponentType();
            Object array = Array.newInstance(c, arrayLength);
            for (int i = 0; i < arrayLength; i++) {
                Array.set(array, i, toObject(jsonNode.items[i], c));
            }
            return (T) array;
        }
        else if (destClass.isPrimitive()) {
            if (jsonNode.isNull()) {
                throw new IllegalStateException("jsonNode was null which cannot assign to a primitive type. Consider using wrapped type to receive this value.");
            }
            if (!jsonNode.isPlain()) {
                throw new IllegalStateException("jsonNode was not an plain value.");
            }
            switch (destClass.getName()){
                case "boolean":
                    return (T) Boolean.valueOf(jsonNode.value);
                case "byte":
                    return (T) Byte.valueOf(jsonNode.value);
                case "char":
                    if (jsonNode.value.length() == 0) {
                        throw new IllegalArgumentException("jsonNode was not refer to a char.");
                    }
                    return (T) Character.valueOf(jsonNode.value.charAt(0));
                case "short":
                    return (T) Short.valueOf(jsonNode.value);
                case "int":
                    return (T) Integer.valueOf(jsonNode.value);
                case "long":
                    return (T) Long.valueOf(jsonNode.value);
                case "float":
                    return (T) Float.valueOf(jsonNode.value);
                case "double":
                    return (T) Double.valueOf(jsonNode.value);
                default:
                    //unreachable
                    return null;
            }
        }
        else if (destClass.isAssignableFrom(String.class)) {
            if (jsonNode.isNull()) {
                return null;
            }
            if (!jsonNode.isPlain()) {
                throw new IllegalStateException("jsonNode was not an plain value.");
            }
            return (T) jsonNode.value;
        }
        else if (Collection.class.isAssignableFrom(destClass)) {
            if (jsonNode.isNull()) {
                return null;
            }
            if (!jsonNode.isArray()) {
                throw new IllegalStateException("jsonNode was not an array.");
            }
            int arrayLength = jsonNode.getLength();
            Collection collection;
            try {
                collection = (Collection) destClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
            for (int i = 0; i < arrayLength; i++) {
                collection.add(toObject(jsonNode.items[i], Object.class));
            }
            return (T) collection;
        }
        else {
            if (jsonNode.isNull()) {
                return null;
            }
            if (!jsonNode.isObject()) {
                throw new IllegalStateException("jsonNode was not an object value.");
            }
            //按Field填充
            T obj;
            try {
                obj = destClass.getConstructor().newInstance();
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
            for (Field field : destClass.getFields()) {
                JsonNode value = jsonNode.fields.get(field.getName());
                if (value != null) {
                    try {
                        field.set(obj, toObject(value, field.getType()));
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
            return obj;
        }
    }

    //字符串处理

    public static JsonNode parseJson(String source) throws ParseException {
        return parseJsonNode(source, new int[1], new StringBuilder());
    }

    private static JsonNode parseJsonNode(String source, int[] offset, StringBuilder bufferReuse) throws ParseException {
        LinkedHashMap<String, JsonNode> buildingFields = null;
        ArrayList<JsonNode> buildingItems = null;
        String buildingKey = null;
        int stateNumber = 0;
        while (offset[0] < source.length()) {
            char c = source.charAt(offset[0]);
            switch (stateNumber) {
                case 0:
                    //吃空白，等待值识别符状态
                    switch (c) {
                        case ' ':
                        case '\n':
                        case '\r':
                        case '\t':
                            offset[0]++;
                            break;
                        case '{':
                            //对象值的开始
                            offset[0]++;
                            buildingFields = new LinkedHashMap<>();
                            stateNumber = 1;
                            break;
                        case '[':
                            //数组值的开始
                            offset[0]++;
                            buildingItems = new ArrayList<>();
                            stateNumber = 4;
                            break;
                        case '"':
                            offset[0]++;
                            return new JsonNode(parseQuotedString(source, offset, '"', bufferReuse), false);
                        case '\'':
                            offset[0]++;
                            return new JsonNode(parseQuotedString(source, offset, '\'', bufferReuse), false);
                        case '}':
                        case ']':
                            throw new ParseException("不允许出现的符号'" + c + "'", offset[0]);
                        default:
                            return new JsonNode(parseUnquotedString(source, offset, false, bufferReuse), true);
                    }
                    break;
                case 1:
                    //等待成员名称状态
                    switch (c) {
                        case ' ':
                        case '\n':
                        case '\r':
                        case '\t':
                            offset[0]++;
                            break;
                        case '"':
                            offset[0]++;
                            buildingKey = parseQuotedString(source, offset, '"', bufferReuse);
                            stateNumber = 2;
                            break;
                        case '\'':
                            offset[0]++;
                            buildingKey = parseQuotedString(source, offset, '\'', bufferReuse);
                            if (buildingKey.length() == 0) {
                                throw new ParseException("不允许空长的字段名称", offset[0]);
                            }
                            stateNumber = 2;
                            break;
                        case '{':
                        case '[':
                        case ']':
                        case ',':
                        case ':':
                            throw new ParseException("不允许出现的符号'" + c + "'", offset[0]);
                        case '}':
                            offset[0]++;
                            return new JsonNode(buildingFields);
                        default:
                            buildingKey = parseUnquotedString(source, offset, true, bufferReuse);
                            if (buildingKey.length() == 0) {
                                throw new ParseException("不允许空长的字段名称", offset[0]);
                            }
                            stateNumber = 2;
                            break;
                    }
                    break;
                case 2:
                    //成员名称读取完毕状态
                    switch (c) {
                        case ' ':
                        case '\n':
                        case '\r':
                        case '\t':
                            offset[0]++;
                            break;
                        case ':':
                            offset[0]++;
                            buildingFields.put(buildingKey, parseJsonNode(source, offset, bufferReuse));
                            stateNumber = 3;
                            break;
                        case '}':
                        case ',':
                            buildingFields.put(buildingKey, new JsonNode("null", true));
                            stateNumber = 3;
                            break;
                        default:
                            throw new ParseException("不允许出现的符号'" + c + "'", offset[0]);
                    }
                    break;
                case 3:
                    //成员值读取完毕状态
                    switch (c) {
                        case ' ':
                        case '\n':
                        case '\r':
                        case '\t':
                            offset[0]++;
                            break;
                        case '}':
                            offset[0]++;
                            return new JsonNode(buildingFields);
                        case ',':
                            offset[0]++;
                            stateNumber = 1;
                            break;
                        default:
                            throw new ParseException("不允许出现的符号'" + c + "'", offset[0]);
                    }
                    break;
                case 4:
                    //等待数组成员值状态
                    switch (c) {
                        case ' ':
                        case '\n':
                        case '\r':
                        case '\t':
                            offset[0]++;
                            break;
                        case ']':
                            stateNumber = 5;
                            break;
                        case '}':
                            throw new ParseException("不允许出现的符号'" + c + "'", offset[0]);
                        case ',':
                            buildingItems.add(new JsonNode("null", true));
                            break;
                        default:
                            buildingItems.add(parseJsonNode(source, offset, bufferReuse));
                            stateNumber = 5;
                            break;
                    }
                    break;
                case 5:
                    //读完成员值状态
                    switch (c) {
                        case ' ':
                        case '\n':
                        case '\r':
                        case '\t':
                            offset[0]++;
                            break;
                        case ']':
                            offset[0]++;
                            return new JsonNode(buildingItems);
                        case ',':
                            offset[0]++;
                            stateNumber = 4;
                            break;
                        default:
                            throw new ParseException("不允许出现的符号'" + c + "'", offset[0]);
                    }
                    break;
            }
        }
        switch (stateNumber) {
            case 1:
            case 2:
            case 3:
                return new JsonNode(buildingFields);
            case 4:
            case 5:
                return new JsonNode(buildingItems);
            default:
                return new JsonNode("null", true);
        }
    }

    //java中char的取值范围为[0, 65535]，因此返回-1代表没有有效字符
    private static int parseEscapeChar(String source, int[] offset) {
        int stateNumber = 0;
        int buildingChar = 0;
        int charCount = 0;
        if (offset[0] == source.length()) {
            return -1;
        }
        while (offset[0] < source.length()) {
            char c = source.charAt(offset[0]);
            switch (stateNumber) {
                case 0:
                    //识别转义类型状态
                    if ('0' <= c && c < '8') {
                        buildingChar = 0;
                        charCount = 0;
                        stateNumber = 1;
                    }
                    else {
                        switch (c) {
                            case 'b':
                                offset[0]++;
                                return '\b';
                            case 'f':
                                offset[0]++;
                                return '\f';
                            case 'n':
                                offset[0]++;
                                return '\n';
                            case 'r':
                                offset[0]++;
                                return '\r';
                            case 't':
                                offset[0]++;
                                return '\t';
                            case 'u':
                                offset[0]++;
                                buildingChar = 0;
                                charCount = 0;
                                stateNumber = 2;
                                break;
                            case '"':
                                offset[0]++;
                                return '"';
                            case '\'':
                                offset[0]++;
                                return '\'';
                            case '\\':
                                offset[0]++;
                                return '\\';
                            default:
                                return -1;
                        }
                    }
                    break;
                case 1:
                    //识别8进制转义字符状态
                    //规则：最多识别3个且范围不超过255的8进制位
                    if ('0' <= c && c < '8') {
                        int newChar = (buildingChar << 3) | (c - '0');
                        if (newChar > 255) {
                            return buildingChar;
                        }
                        buildingChar = newChar;
                        charCount++;
                        offset[0]++;
                    }
                    else {
                        if (charCount == 0) {
                            return -1;
                        }
                        return buildingChar;
                    }
                    if (charCount == 3) {
                        return buildingChar;
                    }
                    break;
                case 2:
                    //识别16进制转义字符状态
                    //规则：最多识别4个16进制位
                    if ('0' <= c && c < '9') {
                        buildingChar = (buildingChar << 4) | (c - '0');
                        offset[0]++;
                        charCount++;
                    }
                    else if ('A' <= c && c < 'F') {
                        buildingChar = (buildingChar << 4) | (c + 10 - 'A');
                        offset[0]++;
                        charCount++;
                    }
                    else if ('a' <= c && c < 'f') {
                        buildingChar = (buildingChar << 4) | (c + 10 - 'a');
                        offset[0]++;
                        charCount++;
                    }
                    else {
                        if (charCount == 0) {
                            return -1;
                        }
                        return buildingChar;
                    }
                    if (charCount == 4) {
                        return buildingChar;
                    }
                    break;
            }
        }
        return 0;
    }

    private static String parseQuotedString(String source, int[] offset, char match, StringBuilder bufferReuse) {
        bufferReuse.delete(0, bufferReuse.length());
        while (offset[0] < source.length()) {
            char c = source.charAt(offset[0]++);
            if (c == match) {
                break;
            }
            else if (c == '\\') {
                int b = parseEscapeChar(source, offset);
                if (b != -1) {
                    bufferReuse.append((char)b);
                }
            }
            else {
                bufferReuse.append(c);
            }
        }
        return bufferReuse.toString();
    }

    private static String parseUnquotedString(String source, int[] offset, boolean terminateAtColon, StringBuilder bufferReuse) {
        bufferReuse.delete(0, bufferReuse.length());
        int escaped;
        while (offset[0] < source.length()) {
            char c = source.charAt(offset[0]);
            switch (c) {
                case ']':
                case '}':
                case ',':
                    return bufferReuse.toString().trim();
                case ':':
                    if (terminateAtColon) {
                        return bufferReuse.toString().trim();
                    }
                    else {
                        offset[0]++;
                        bufferReuse.append(c);
                    }
                    break;
                case '\\':
                    offset[0]++;
                    escaped = parseEscapeChar(source, offset);
                    if (escaped != -1) {
                        bufferReuse.append((char)escaped);
                    }
                    break;
                default:
                    offset[0]++;
                    bufferReuse.append(c);
                    break;
            }
        }
        return bufferReuse.toString().trim();
    }

    @Override
    public String toString() {
        return toString(null);
    }

    public String toString(String indent) {
        StringBuilder out = new StringBuilder();
        toStringInner(indent, 0, out);
        return out.toString();
    }

    private void toStringInner(String indent, int level, StringBuilder out) {
        switch (type) {
            case TYPE_PLAIN:
                if (simple) {
                    out.append(value);
                }
                else {
                    out.append("\"").append(value
                            .replace("\\", "\\\\")
                            .replace("\r", "\\r")
                            .replace("\n", "\\n")
                            .replace("\t", "\\t")
                            .replace("\"", "\\\"")
                    ).append("\"");
                }
                break;
            case TYPE_OBJECT:
                out.append("{");
                if (!fields.isEmpty()) {
                    if (indent != null) {
                        out.append(System.lineSeparator());
                    }
                    boolean first = true;
                    for (Map.Entry<String, JsonNode> field : fields.entrySet()) {
                        if (first) {
                            first = false;
                        } else {
                            out.append(",");
                            if (indent == null) {
                                out.append(" ");
                            }
                            else {
                                out.append(System.lineSeparator());
                            }
                        }
                        if (indent != null) {
                            out.append(com.davidsoft.compact.String.repeat(indent, level + 1));
                        }
                        out.append("\"").append(field.getKey()).append("\": ");
                        field.getValue().toStringInner(indent, level + 1, out);
                    }
                    if (indent != null) {
                        out.append(System.lineSeparator());
                        out.append(com.davidsoft.compact.String.repeat(indent, level));
                    }
                }
                out.append("}");
                break;
            case TYPE_ARRAY:
                out.append("[");
                if (items.length > 0) {
                    if (indent != null) {
                        out.append(System.lineSeparator());
                    }
                    boolean first = true;
                    for (JsonNode item : items) {
                        if (first) {
                            first = false;
                        } else {
                            out.append(",");
                            if (indent == null) {
                                out.append(" ");
                            }
                            else {
                                out.append(System.lineSeparator());
                            }
                        }
                        if (indent != null) {
                            out.append(com.davidsoft.compact.String.repeat(indent, level + 1));
                        }
                        item.toStringInner(indent, level + 1, out);
                    }
                    if (indent != null) {
                        out.append(System.lineSeparator());
                        out.append(com.davidsoft.compact.String.repeat(indent, level));
                    }
                }
                out.append("]");
                break;
        }
    }
}
