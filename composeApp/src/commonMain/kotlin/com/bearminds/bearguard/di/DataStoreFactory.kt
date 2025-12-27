package com.bearminds.bearguard.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

/**
 * Factory for creating DataStore instances.
 */
object DataStoreFactory {

    private const val DATASTORE_FILE = "bearguard_prefs.preferences_pb"

    fun create(producePath: () -> String): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            produceFile = { producePath().toPath() }
        )
    }
}
