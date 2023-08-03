package ru.com.bulat.trackergps.utils

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.com.bulat.trackergps.R


fun Fragment.openFragment(f: Fragment) {
    (activity as AppCompatActivity).supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(R.id.paceHolder, f)
        .commit()
}

fun AppCompatActivity.openFragment(f: Fragment) {
    if (supportFragmentManager.fragments.isNotEmpty()){
        if (supportFragmentManager.fragments[0].javaClass == f.javaClass) {
            return
        }
    }
    supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(R.id.paceHolder, f)
        .commit()
}

fun Fragment.showToast(s: String) {
    Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.showToast(s: String) {
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}