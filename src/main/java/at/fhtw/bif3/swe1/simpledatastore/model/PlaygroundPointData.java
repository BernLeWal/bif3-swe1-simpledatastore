package at.fhtw.bif3.swe1.simpledatastore.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Optional;

// https://projectlombok.org/features/Data

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
