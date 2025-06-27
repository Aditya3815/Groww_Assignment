package com.example.groww_assignment.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.groww_assignment.data.local.database.dao.StockDao
import com.example.groww_assignment.data.local.database.dao.WatchlistDao
import com.example.groww_assignment.data.local.database.entities.StockEntity
import com.example.groww_assignment.data.local.database.entities.WatchlistEntity
import com.example.groww_assignment.data.local.database.entities.WatchlistStockCrossRef
import com.example.groww_assignment.utils.Constants.DATABASE_VERSION


@Database(
    entities = [
        StockEntity::class,
        WatchlistEntity::class,
        WatchlistStockCrossRef::class
    ],
    version = DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class StocksDatabase : RoomDatabase() {
    abstract fun stockDao(): StockDao
    abstract fun watchlistDao(): WatchlistDao
}