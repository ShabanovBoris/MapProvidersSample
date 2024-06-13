package ru.bosha.mapssample

import MapProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.bosha.mapssample.databinding.FragmentMapListDialogBinding
import ru.bosha.mapssample.databinding.MapListItemBinding


class MapListDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentMapListDialogBinding? = null
    private val binding get() = _binding!!

    private var mapProvider: MapProvider? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = ItemAdapter(12)
        dialog?.setOnShowListener {
            (dialog as? BottomSheetDialog)?.behavior?.apply {
                state = BottomSheetBehavior.STATE_HALF_EXPANDED
                isDraggable = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class ViewHolder(val binding: MapListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    private inner class ItemAdapter(private val itemCount: Int) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                MapListItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            mapProvider = MapProvider(binding.root.context, defaultVendor)
            mapProvider?.provide(
                holder = holder.binding.mapHolder,
                lifecycleOwner = this@MapListDialogFragment,
                interactive = false
            ) { map ->
                // ... map is ready
            }
        }

        override fun getItemCount(): Int = itemCount
    }
}