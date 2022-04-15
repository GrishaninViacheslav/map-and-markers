package io.github.grishaninvyacheslav.map_and_markers.entities

import java.lang.RuntimeException

class NoKnownLastLocationException: RuntimeException("В памяти устройства нет данных о поледней известной позиции")