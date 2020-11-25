package at.fhtw.bif3.swe1.simpledatastore;

import lombok.Data;

import java.io.Serializable;
import java.util.Optional;

@Data
public class PlaygroundPointData implements Serializable {
    private String fId;
    private Integer objectId;
    private String shape;
    private String anlName;
    private Integer bezirk;
    private String spielplatzDetail;
    private String typDetail;
    private String seAnnoCadData;
}
