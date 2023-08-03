package ru.com.bulat.trackergps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.com.bulat.trackergps.databinding.ActivityMainBinding
import ru.com.bulat.trackergps.fragments.MainFragment
import ru.com.bulat.trackergps.fragments.SettingsFragment
import ru.com.bulat.trackergps.fragments.TracksFragment
import ru.com.bulat.trackergps.utils.openFragment

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