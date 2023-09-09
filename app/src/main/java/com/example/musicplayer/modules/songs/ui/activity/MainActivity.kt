package com.example.musicplayer.modules.songs.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.modules.core.utils.viewBinding
import com.example.musicplayer.modules.songs.ui.fragments.SongFragment
import com.example.musicplayer.modules.songs.ui.fragments.SongPlayerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    var isShowRecyclerViewAnimation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        if (savedInstanceState != null) {
            supportFragmentManager.getFragment(savedInstanceState, "myFragmentName")?.let {
                addFragment(it)
            }
        } else {
            addFragment(SongFragment.newInstance())
        }
    }

    fun addFragment(fragment: Fragment) {
        fragment.arguments = getArgumentsForFragment(fragment)
        supportFragmentManager
            .beginTransaction()
            .replace(binding.frameActivityMain.id, fragment, fragment::class.java.simpleName)
            .setCustomAnimations(
                R.anim.fragment_right_to_left,
                R.anim.fragment_left_to_right,
                R.anim.fragment_right_to_left,
                R.anim.fragment_left_to_right
            )
            .addToBackStack(null)
            .commit()
    }

    private fun getArgumentsForFragment(fragment: Fragment): Bundle {
        val b = Bundle()
        if (fragment is SongFragment){
            b.putBoolean("SHOW_RECYCLER_VIEW_ANIMATION", true)
        }
        return b
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(binding.frameActivityMain.id)
        when (fragment?.tag) {
            SongFragment::class.java.simpleName -> {
                this.finish()
            }
            SongPlayerFragment::class.java.simpleName -> {
                isShowRecyclerViewAnimation = false
                supportFragmentManager.popBackStack()
//                super.onBackPressed()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)

        supportFragmentManager.findFragmentById(binding.frameActivityMain.id)?.let {
            supportFragmentManager.putFragment(outState, "myFragmentName", it)
        }
    }
}