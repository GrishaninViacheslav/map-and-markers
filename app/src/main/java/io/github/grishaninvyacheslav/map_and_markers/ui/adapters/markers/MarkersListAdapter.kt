package io.github.grishaninvyacheslav.map_and_markers.ui.adapters.markers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.grishaninvyacheslav.map_and_markers.databinding.ItemMarkerBinding

class MarkersListAdapter(
    private val dataModel: IMarkersDataModel,
    var onItemClick: ((view: IMarkerItemView) -> Unit),
    var onEditClick: ((view: IMarkerItemView) -> Unit)
) :
    RecyclerView.Adapter<MarkerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MarkerViewHolder(
            ItemMarkerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClick,
            onEditClick
        )

    override fun getItemCount() = dataModel.getCount()

    override fun onBindViewHolder(holderMarker: MarkerViewHolder, position: Int) =
        dataModel.bindView(holderMarker.apply { pos = position })
}