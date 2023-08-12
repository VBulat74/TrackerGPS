package ru.com.bulat.trackergps.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.com.bulat.trackergps.MainApp
import ru.com.bulat.trackergps.MainViewModel
import ru.com.bulat.trackergps.databinding.FragmentTracksBinding
import ru.com.bulat.trackergps.db.TrackAdapter

class TracksFragment : Fragment() {

    private lateinit var binding : FragmentTracksBinding
    private lateinit var adapter : TrackAdapter

    private val viewModel : MainViewModel by activityViewModels {
        MainViewModel.ViewModelFactory(
            (requireContext().applicationContext as MainApp).database
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycleView()
        getListFromDb()
    }

    private fun initRecycleView() = with(binding){
        adapter = TrackAdapter()
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter
    }

    private fun getListFromDb() {
        viewModel.tracks.observe(viewLifecycleOwner){ trackItemList ->
            adapter.submitList(trackItemList)

            if (trackItemList.isEmpty()) {binding.tvEmpty.visibility = View.VISIBLE}
            else {binding.tvEmpty.visibility = View.GONE}
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TracksFragment()
    }
}