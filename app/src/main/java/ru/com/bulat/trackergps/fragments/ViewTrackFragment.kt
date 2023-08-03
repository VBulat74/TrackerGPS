package ru.com.bulat.trackergps.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.com.bulat.trackergps.databinding.FragmentViewTrackBinding

class ViewTrackFragment : Fragment() {

    private lateinit var binding : FragmentViewTrackBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentViewTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun mainInstance() = ViewTrackFragment()
    }
}