package at.fhtw.bif3.swe1.simpledatastore;

import lombok.Data;

import java.io.Serializable;
import java.util.Optional;

@Data
public class PlaygroundPointData implements Serializable {
    private final String fId;
    private final Integer objectId;
    private final String shape;
    private final String anlName;
    private final Integer bezirk;
    private final String spielplatzDetail;
    private final String typDetail;
    private final String seAnnoCadData;
}
