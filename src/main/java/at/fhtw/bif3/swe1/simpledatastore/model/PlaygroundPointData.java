package at.fhtw.bif3.swe1.simpledatastore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
