package ru.com.bulat.trackergps.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import ru.com.bulat.trackergps.MainApp
import ru.com.bulat.trackergps.MainViewModel
import ru.com.bulat.trackergps.databinding.FragmentViewTrackBinding

class ViewTrackFragment : Fragment() {

    private lateinit var binding : FragmentViewTrackBinding

    private val viewModel : MainViewModel by activityViewModels {
        MainViewModel.ViewModelFactory(
            (requireContext().applicationContext as MainApp).database
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsOSM()
        // Inflate the layout for this fragment
        binding = FragmentViewTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getTrack()
    }

    private fun getTrack() = with (binding) {
        viewModel.currentTrack.observe(viewLifecycleOwner) { trackItem ->
            tvData.text = trackItem.date
            tvTime.text = trackItem.time
            tvAvrVelocity.text = trackItem.velocity
            tvDistance.text = trackItem.distance

            val polyline = getPolyline(trackItem.geoPoints)
            map.overlays.add(polyline)
            goToStartPosition(polyline)
        }
    }

    private fun goToStartPosition(polyline: Polyline) {
        val center = GeoPoint(polyline.bounds.centerLatitude, polyline.bounds.centerLongitude)
        binding.map.controller.zoomTo(18.0)
        binding.map.controller.animateTo(center)

        //binding.map.zoomToBoundingBox(polyline.bounds, true)
    }

    private fun getPolyline (strGeoPoints : String) : Polyline {
        val polyline = Polyline()
        val list = strGeoPoints.split("/")
        list.forEach {
            if (it.isEmpty()) return@forEach
            val points = it.split(",")
            polyline.addPoint(GeoPoint(points[0].toDouble(), points[1].toDouble()))
        }
        return polyline
    }

    private fun settingsOSM() {
        Configuration
            .getInstance()
            .load(
                (activity as AppCompatActivity),
                activity?.getSharedPreferences("osm_pref", Context.MODE_PRIVATE),
            )

        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME
    }

    companion object {
        @JvmStatic
        fun mainInstance() = ViewTrackFragment()
    }
}