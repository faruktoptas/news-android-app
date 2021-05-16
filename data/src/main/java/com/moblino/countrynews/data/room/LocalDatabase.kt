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
                            //.addMigrations(Migration2To3())
                            .fallbackToDestructiveMigrationFrom(2)
                            .build()

                }
            }
            return instance
        }
    }
}


class Migration2To3 : Migration(2, 3) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // TODO: Migrate favorites
        /*val cursor = database.query("Select * from Favourites")
        cursor.moveToFirst()
        do {
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val description = cursor.getString(cursor.getColumnIndex("description"))
            val feedId = cursor.getInt(cursor.getColumnIndex("feedId"))
            val feedTitle = cursor.getString(cursor.getColumnIndex("feedTitle"))
            val image = cursor.getString(cursor.getColumnIndex("image"))
            val link = cursor.getString(cursor.getColumnIndex("link"))
            val pubDate = cursor.getString(cursor.getColumnIndex("pubDate"))
            val title = cursor.getString(cursor.getColumnIndex("title"))

            val values = ContentValues()
            values.put("description", description)
            values.put("feedId", feedId)
            values.put("feedTitle", feedTitle)
            values.put("image", image)
            values.put("link", link)
            values.put("pubDate", pubDate)
            values.put("title", title)

            database.insert("fav", CONFLICT_REPLACE, values)

            Log.v("asd", "$id $description $feedId $image $feedTitle $image $link $pubDate $title")

        } while (cursor.moveToNext())

        throw RuntimeException("not handled")*/
        /*database.execSQL("ALTER TABLE favorite ADD COLUMN readTime INTEGER NOT NULL DEFAULT 0;")
        Timber.i("Migrated from 2 to 3")*/
    }

}
