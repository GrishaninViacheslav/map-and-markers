package io.github.grishaninvyacheslav.map_and_markers.ui.adapters.markers

import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import io.github.grishaninvyacheslav.map_and_markers.databinding.ItemMarkerBinding

class MarkerViewHolder(
    private val binding: ItemMarkerBinding,
    private var onItemClick: ((view: IMarkerItemView) -> Unit)?,
    private var onEditClick: ((view: IMarkerItemView) -> Unit)?
) :
    RecyclerView.ViewHolder(binding.root),
    IMarkerItemView {
    init {
        itemView.setOnClickListener { onItemClick?.invoke(this) }
        binding.editOptions.setOnClickListener { onEditClick?.invoke(this) }
    }

    override var pos = -1

    override fun setTitle(title: String) {
        binding.markerTitle.text = title
    }

    override fun setCoordinates(coordinates: LatLng) {
        binding.coordinates.text = coordinates.toString()
    }
}