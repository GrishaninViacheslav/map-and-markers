package io.github.grishaninvyacheslav.map_and_markers.view_models.map

sealed class LocalRepositoryState {
    object InProgress: LocalRepositoryState()
    data class Error(val message: String?): LocalRepositoryState()
    object Success: LocalRepositoryState()
}