package at.fhtw.bif3.swe1.simpledatastore;

import java.util.Objects;

public class PlaygroundPoint {
    private final String fId;
    private final Integer objectId;
    private final String shape;
    private final String anlName;
    private final Integer bezirk;
    private final String spielplatzDetail;
    private final String typDetail;
    private final String seAnnoCadData;

    public PlaygroundPoint(String fId, Integer objectId, String shape, String anlName, Integer bezirk, String spielplatzDetail, String typDetail, String seAnnoCadData) {
        this.fId = fId;
        this.objectId = objectId;
        this.shape = shape;
        this.anlName = anlName;
        this.bezirk = bezirk;
        this.spielplatzDetail = spielplatzDetail;
        this.typDetail = typDetail;
        this.seAnnoCadData = seAnnoCadData;
    }

    public String getfId() {
        return fId;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public String getShape() {
        return shape;
    }

    public String getAnlName() {
        return anlName;
    }

    public Integer getBezirk() {
        return bezirk;
    }

    public String getSpielplatzDetail() {
        return spielplatzDetail;
    }

    public String getTypDetail() {
        return typDetail;
    }

    public String getSeAnnoCadData() {
        return seAnnoCadData;
    }

    @Override
    public String toString() {
        return "PlaygroundPoint{" +
                "fId='" + fId + '\'' +
                ", objectId=" + objectId +
                ", shape='" + shape + '\'' +
                ", anlName='" + anlName + '\'' +
                ", bezirk=" + bezirk +
                ", spielplatzDetail='" + spielplatzDetail + '\'' +
                ", typDetail='" + typDetail + '\'' +
                ", seAnnoCadData='" + seAnnoCadData + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaygroundPoint that = (PlaygroundPoint) o;
        return Objects.equals(fId, that.fId) &&
                Objects.equals(objectId, that.objectId) &&
                Objects.equals(shape, that.shape) &&
                Objects.equals(anlName, that.anlName) &&
                Objects.equals(bezirk, that.bezirk) &&
                Objects.equals(spielplatzDetail, that.spielplatzDetail) &&
                Objects.equals(typDetail, that.typDetail) &&
                Objects.equals(seAnnoCadData, that.seAnnoCadData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fId, objectId, shape, anlName, bezirk, spielplatzDetail, typDetail, seAnnoCadData);
    }
}
