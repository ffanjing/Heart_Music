package Utils;

import JavaBean.Song;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yanzhang2 on 2017/4/17.
 */
public class SongUtil {

    public static Song createSongFromResultSet(ResultSet set) {
        Song song = new Song();
        try {
            song.setSongID(set.getInt(1));
            song.setSongName(set.getString(2));
            song.setSongArtist(set.getString(3));
            song.setSongAlbum(set.getString(4));
            song.setSongURL(set.getString(5));
            song.setSongCover(set.getString(6));
            song.setSongTags(set.getString(7));
            song.setSongCount(set.getInt(8));
            return song;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

}
