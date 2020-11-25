package at.fhtw.bif3.swe1.simpledatastore;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataStoreXML extends AbstractDataStore {

    /**
     * Helper class to define the names of the XML-elements
     */
    public static class PlaygroundPoints {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "PlaygroundPoint")
        public List<PlaygroundPointRecord> records;
    }

    @Override
    public List<PlaygroundPointRecord> read() {
        // !!!!! unfortunately Jackson library does not support records
        throw new UnsupportedOperationException("Java15-preview records are not supported");
    }

    @Override
    public void write(List<PlaygroundPointRecord> data) {
        PlaygroundPoints serializedData = new PlaygroundPoints();
        serializedData.records = data;

        XmlMapper mapper = new XmlMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            mapper.writeValue(output, serializedData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<PlaygroundPointData> readData() {
        XmlMapper mapper = new XmlMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            return mapper.readValue(input, new TypeReference<ArrayList<PlaygroundPointData>>(){} );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
