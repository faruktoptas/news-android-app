package com.moblino.countrynews.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.moblino.countynews.common.model.RssItem


@Database(
    entities = [
        RssItem::class
    ], exportSchema = false, version = 3
)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao


    companion object {
        private const val DB_NAME = "favourites.db"
        private var instance: LocalDatabase? = null

        fun getDatabase(context: Context): LocalDatabase? {
            if (instance == null) {
                synchronized(LocalDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LocalDatabase::class.java,
                        DB_NAME
                    )
                        .addMigrations(Migration2To3())
                        .build()

                }
            }
            return instance
        }
    }
}


class Migration2To3 : Migration(2, 3) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE fav_new (id INTEGER NOT NULL, image TEXT, feedId INTEGER NOT NULL, link TEXT NOT NULL, description TEXT, title TEXT, feedTitle TEXT, pubDate TEXT NOT NULL, PRIMARY KEY(id))")
        database.execSQL("INSERT INTO fav_new (id, image, feedId, link, description, title, feedTitle, pubDate) SELECT id, image, feedId, link, description, title, feedTitle, pubDate FROM Favourites")
        database.execSQL("DROP TABLE Favourites")
        database.execSQL("ALTER TABLE fav_new RENAME TO Favourites")
    }

}
