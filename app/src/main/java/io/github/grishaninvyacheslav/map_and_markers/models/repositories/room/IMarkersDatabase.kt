package io.github.grishaninvyacheslav.map_and_markers.models.repositories.room

interface IMarkersDatabase {
    fun markersDao(): MarkersDao
}