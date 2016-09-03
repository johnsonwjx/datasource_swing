package youngfriend.bean;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xiong on 15/7/17.
 */
public class BeanDto {
    private static final Logger logger = LoggerFactory.getLogger(BeanDto.class);
    private JsonObject values = null;
    private String toString = "";

    public BeanDto() {
        values = new JsonObject();
    }

    public BeanDto(JsonObject value, String... toString) {
        this.values = value;
        setToString(toString);
    }

    public BeanDto(String key, String value) {
        this.values = new JsonObject();
        setItem("key", key);
        setItem("value", value);
        setToString("key");
    }

    public void setItem(String name, String value) {
        values.addProperty(name, value);
    }

    public void removeItem(String name) {
        values.remove(name);
    }

    public String getValue(String name) {
        if (values.has(name)) {
            return values.get(name).getAsString();
        } else {
            return "";
        }
    }

    public void setToString(String... toString) {
        StringBuilder sb = new StringBuilder();
        for (String str : toString) {
            String item = getValue(str);
            if (Strings.isNullOrEmpty(item)) {
                continue;
            }
            sb.append(item).append("-");
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        this.toString = sb.toString();
    }

    @Override
    public String toString() {
        return toString;
    }
}
