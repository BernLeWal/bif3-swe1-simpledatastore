package at.fhtw.bif3.swe1.simpledatastore.model;

import java.io.Serializable;

/**
 * Implementation of a PlaygroundPoint with Java15-Preview Feature: record
 * Background info: https://jax.de/blog/datenklassen-in-java-einfuehrung-in-java-records/
 * @see java.lang.Record
 */
public record PlaygroundPointRecord(
        String fId,
        Integer objectId,
        String shape,
        String anlName,
        Integer bezirk,
        String spielplatzDetail,
        String typDetail,
        String seAnnoCadData) implements Serializable /* necessary for Object-Streams */
{
}
