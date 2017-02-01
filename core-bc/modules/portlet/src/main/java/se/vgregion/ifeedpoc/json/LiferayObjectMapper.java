package se.vgregion.ifeedpoc.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.liferay.portal.model.BaseModel;
import com.liferay.portlet.expando.model.ExpandoBridge;

public class LiferayObjectMapper extends ObjectMapper {

    public LiferayObjectMapper() {
        super();
    }
}