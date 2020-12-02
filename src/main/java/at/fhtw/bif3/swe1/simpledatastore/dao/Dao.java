package at.fhtw.bif3.swe1.simpledatastore.dao;

import java.util.Collection;
import java.util.Optional;

public interface Dao<T> {

    Optional<T> get(int id);

    Collection<T> getAll();

    void save(T t);

    void update(T t, String[] params);

    void delete(T t);
}
