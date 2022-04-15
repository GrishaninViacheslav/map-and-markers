package io.github.grishaninvyacheslav.map_and_markers.use_cases.map

import io.github.grishaninvyacheslav.map_and_markers.models.providers.GMapApiErrorProvider
import io.reactivex.rxjava3.core.Maybe

class GMapApiErrorUseCase(private val gMapApiErrorProvider: GMapApiErrorProvider = GMapApiErrorProvider(1000)) {
    var errorMessage: Maybe<String> =
        gMapApiErrorProvider.apiErrorLog.flatMap { errorLog ->
            Maybe.just(errorLog.substringAfter("${gMapApiErrorProvider.errorTag}:"))
        }
}