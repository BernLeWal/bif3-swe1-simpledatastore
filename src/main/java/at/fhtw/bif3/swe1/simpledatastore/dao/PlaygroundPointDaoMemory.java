package at.fhtw.bif3.swe1.simpledatastore.dao;

import at.fhtw.bif3.swe1.simpledatastore.model.PlaygroundPointData;

import java.util.*;

public class PlaygroundPointDaoMemory implements Dao<PlaygroundPointData> {
    private Map<Integer, PlaygroundPointData> playgroundPoints = new HashMap<>();

    @Override
    public Optional<PlaygroundPointData> get(int id) {
        return Optional.ofNullable( playgroundPoints.get(id) );
    }

    @Override
    public Collection<PlaygroundPointData> getAll() {
        return playgroundPoints.values();
    }

    @Override
    public void save(PlaygroundPointData playgroundPoint) {
        playgroundPoints.put(playgroundPoint.getObjectId(), playgroundPoint);
    }

    @Override
    public void update(PlaygroundPointData playgroundPoint, String[] params) {
        playgroundPoint.setFId( Objects.requireNonNull( params[0], "fId cannot be null" ) );
        playgroundPoint.setObjectId( Integer.parseInt(Objects.requireNonNull( params[1], "ObjectId cannot be null" ) ) );
        playgroundPoint.setShape( Objects.requireNonNull( params[2] ) );
        playgroundPoint.setAnlName( Objects.requireNonNull( params[3] ) );
        playgroundPoint.setBezirk( Integer.parseInt(Objects.requireNonNull( params[4], "Bezirk cannot be null" ) ) );
        playgroundPoint.setSpielplatzDetail( Objects.requireNonNull( params[5], "SpielplatzDetail cannot be null" ) );
        playgroundPoint.setTypDetail( Objects.requireNonNull( params[6], "TypDetail cannot be null" ) );
        playgroundPoint.setSeAnnoCadData( params[7] );

        playgroundPoints.put( playgroundPoint.getObjectId(), playgroundPoint );
    }

    @Override
    public void delete(PlaygroundPointData playgroundPoint) {
        playgroundPoints.remove(playgroundPoint.getObjectId());
    }
}
