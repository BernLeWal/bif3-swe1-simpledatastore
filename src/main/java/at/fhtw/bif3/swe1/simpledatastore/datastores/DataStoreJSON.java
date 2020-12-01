package at.fhtw.bif3.swe1.simpledatastore.datastores;

import at.fhtw.bif3.swe1.simpledatastore.datastores.AbstractDataStore;
import at.fhtw.bif3.swe1.simpledatastore.model.PlaygroundPointData;
import at.fhtw.bif3.swe1.simpledatastore.model.PlaygroundPointRecord;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataStoreJSON extends AbstractDataStore {
    @Override
    public List<PlaygroundPointRecord> read() {
        // !!!!! unfortunately Jackson library does not support records
        throw new UnsupportedOperationException("Java15-preview records are not supported");
    }

    @Override
    public void write(List<PlaygroundPointRecord> data) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            mapper.writeValue(output, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<PlaygroundPointData> readData() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            return mapper.readValue(input, new TypeReference<ArrayList<PlaygroundPointData>>(){} );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
