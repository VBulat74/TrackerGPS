package com.example.trackergps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trackergps.databinding.ActivityMainBinding
import com.example.trackergps.fragments.MainFragment
import com.example.trackergps.fragments.SettingsFragment
import com.example.trackergps.fragments.TracksFragment
import com.example.trackergps.utils.openFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        onBottomNavClick()

        openFragment(MainFragment.newInstance())
    }

    private fun onBottomNavClick() {
        binding.bNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.id_settings -> openFragment(SettingsFragment())
                R.id.id_home -> openFragment(MainFragment.newInstance())
                R.id.id_tracks -> openFragment(TracksFragment.newInstance())
            }
            true
        }
    }
}