package io.github.grishaninvyacheslav.map_and_markers.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.grishaninvyacheslav.map_and_markers.databinding.ActivityMapsBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}