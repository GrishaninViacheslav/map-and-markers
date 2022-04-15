package io.github.grishaninvyacheslav.map_and_markers.ui.fragments.markers

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.MarkerOptions
import io.github.grishaninvyacheslav.map_and_markers.MapAndMarkersApp
import io.github.grishaninvyacheslav.map_and_markers.R
import io.github.grishaninvyacheslav.map_and_markers.databinding.FragmentMarkersBinding
import io.github.grishaninvyacheslav.map_and_markers.ui.activity.IBottomNavigation
import io.github.grishaninvyacheslav.map_and_markers.ui.activity.Screens
import io.github.grishaninvyacheslav.map_and_markers.ui.adapters.markers.IMarkerItemView
import io.github.grishaninvyacheslav.map_and_markers.ui.adapters.markers.IMarkersDataModel
import io.github.grishaninvyacheslav.map_and_markers.ui.adapters.markers.MarkersListAdapter
import io.github.grishaninvyacheslav.map_and_markers.ui.fragments.BaseFragment
import io.github.grishaninvyacheslav.map_and_markers.use_cases.markers.MarkersUseCase
import io.github.grishaninvyacheslav.map_and_markers.view_models.markers.MarkersViewModel
import io.github.grishaninvyacheslav.map_and_markers.view_models.markers.MarkersViewModelFactory

class MarkersFragment : BaseFragment<FragmentMarkersBinding>(FragmentMarkersBinding::inflate) {
    companion object {
        fun newInstance() = MarkersFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.markersListState.observe(viewLifecycleOwner) { renderMarkersList(it) }
        viewModel.markerToRenameNameState.observe(viewLifecycleOwner) {
            showRenameDialog(
                it.first,
                it.second
            )
        }
        viewModel.markerToRemoveNameState.observe(viewLifecycleOwner) {
            showRemoveConfirmDialog(
                it.first,
                it.second
            )
        }
    }

    override fun onFabClick() =
        (requireActivity() as IBottomNavigation).navigateTo(Screens.MarkerCreator)

    private fun renderMarkersList(markers: MutableList<Pair<String, MarkerOptions>>) =
        with(binding) {
            if (adapter == null) {
                initMarkersList(markers)
            } else {
                markersDataModel.markers = markers
            }
            updateMarkersList()
        }

    private val markerRenameDialogFactory by lazy { MarkerRenameDialogFactory(requireContext()) }
    private fun showRenameDialog(indexOfMarkerToRename: Int, oldName: String) {
        markerRenameDialogFactory.create(oldName) { newName ->
            viewModel.renameMarker(
                indexOfMarkerToRename,
                newName
            )
        }.show()
    }

    private val markerRemoveDialogFactory by lazy { MarkerRemoveConfirmDialogFactory() }
    private fun showRemoveConfirmDialog(indexOfMarkerToRemove: Int, markerToRemoveName: String) {
        markerRemoveDialogFactory.create(requireContext(), markerToRemoveName) {
            viewModel.removeMarker(indexOfMarkerToRemove)
        }.show()
    }

    private fun initMarkersList(markers: MutableList<Pair<String, MarkerOptions>>) = with(binding) {
        markersListView.layoutManager = LinearLayoutManager(context)
        adapter = MarkersListAdapter(
            markersDataModel.apply { this.markers = markers },
            onItemClick = { view -> /* TODO("NOT YET IMPLEMENTED") */ },
            onEditClick = { view ->
                EditOptionsDialog(requireContext(), view).apply {
                    onRenameMarkerClick = {
                        viewModel.requestMarkerRename(view.pos)
                    }
                    onRemoveMarkerClick = {
                        viewModel.requestMarkerRemove(view.pos)
                    }
                }.show()
            }
        )
        markersListView.adapter = adapter
    }

    private fun updateMarkersList() {
        adapter?.apply {
            notifyDataSetChanged()
            binding.noMarkers.isVisible = (itemCount == 0)
        }
    }

    private val viewModel: MarkersViewModel by viewModels {
        MarkersViewModelFactory(MarkersUseCase().apply {
            MapAndMarkersApp.instance.appComponent.inject(this)
        })
    }

    private var adapter: MarkersListAdapter? = null

    private val markersDataModel = object : IMarkersDataModel {
        var markers = listOf<Pair<String, MarkerOptions>>()
        override fun getCount() = markers.size
        override fun bindView(view: IMarkerItemView) = with(markers[view.pos].second) {
            view.setTitle(
                if (!title.isNullOrBlank())
                    title!!
                else
                    getString(R.string.marker_without_title)
            )
            view.setCoordinates(position)
        }
    }
}