package at.fhtw.bif3.swe1.simpledatastore.dao;

import java.util.Collection;
import java.util.Optional;

/**
 * Implementation of the Data-Access-Object Pattern
 * DAO overview see: https://www.baeldung.com/java-dao-pattern
 *
 * @param <T>
 */
public interface Dao<T> {

    Optional<T> get(int id);

    Collection<T> getAll();

    void save(T t);

    void update(T t, String[] params);

    void delete(T t);
}
