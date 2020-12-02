package at.fhtw.bif3.swe1.simpledatastore.dao;

import at.fhtw.bif3.swe1.simpledatastore.model.PlaygroundPointData;

import java.util.Optional;

public class DaoDemo {
    private static Dao<PlaygroundPointData> dao;

    public static void main(String[] args) {
        dao = new PlaygroundPointDaoMemory();
        dao.save(new PlaygroundPointData("SPIELPLATZPUNKTOGD.fid-339f4d3f_1762344675f_1e5a",501013,"POINT (16.403510034695213 48.16441803068401)","PA Löwygrube",10,"\"Fußball, Klettern, Rutschen, Schaukeln, Seilbahn, Spielhaus, Wippen\"","\"Ballspielplatz, Kleinkinderspielplatz, Spielplatz\"",""));
        dao.save(new PlaygroundPointData("SPIELPLATZPUNKTOGD.fid-339f4d3f_1762344675f_1e5b",501014,"POINT (16.390483694249703 48.21002482784035)","Ida-Bohatta-Park",3,"\"Balancieren, Basketball, Fußball, Klettern, Reck, Rutschen, Schaukeln\"","\"Ballspielkäfig, Spielplatz\"",""));

        PlaygroundPointData playgroundPoint1 = getPlaygroundPoint(501013);
        System.out.println( playgroundPoint1 );

        dao.update( playgroundPoint1, new String[]
                {"SPIELPLATZPUNKTOGD.fid-339f4d3f_1762344675f_1e5c","501013","POINT (16.365061261939466 48.180602175752234)","Waldmüllerpark","10","\"Basketball, Beachvolleyball, Fußball, Klettern, Reck, Rutschen, Sandspielen, Schaukeln, Seilbahn, Tischtennis, Wippen\"","\"Ballspielkäfig, Ballspielplatz, Kleinkinderspielplatz, Spielplatz\"",""} );
        System.out.println();

        PlaygroundPointData playgroundPoint2 = getPlaygroundPoint( 501014);
        dao.delete( playgroundPoint2 );
        dao.save(new PlaygroundPointData("SPIELPLATZPUNKTOGD.fid-339f4d3f_1762344675f_1e5d",501016,"POINT (16.33061333081481 48.2457148947513)","PA Börnergasse",19,"\"Basketball, Klettern, Rutschen, Sandspielen, Seilbahn, Wippen\"","\"Ballspielkäfig, Kleinkinderspielplatz, Spielplatz\"",""));

        dao.getAll().forEach(item -> System.out.println(item));
    }

    private static PlaygroundPointData getPlaygroundPoint(int id) {
        Optional<PlaygroundPointData> playgroundPoint = dao.get(id);

        return playgroundPoint.orElseGet(
                () -> new PlaygroundPointData()
        );
    }
}
