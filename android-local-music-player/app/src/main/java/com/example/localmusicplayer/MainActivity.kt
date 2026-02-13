package com.example.localmusicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var repository: MusicRepository
    private lateinit var viewModel: SongsViewModel

    private lateinit var adapter: SongAdapter
    private lateinit var titleText: TextView
    private lateinit var playPauseButton: MaterialButton

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            loadSongs()
        } else {
            Toast.makeText(this, getString(R.string.permission_needed), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = MusicRepository(this)
        viewModel = ViewModelProvider(this)[SongsViewModel::class.java]
        player = ExoPlayer.Builder(this).build()

        titleText = findViewById(R.id.titleText)
        playPauseButton = findViewById(R.id.btnPlayPause)

        adapter = SongAdapter(emptyList()) { position ->
            playSongAt(position)
        }

        findViewById<RecyclerView>(R.id.recyclerSongs).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        findViewById<MaterialButton>(R.id.btnPrev).setOnClickListener {
            player.seekToPreviousMediaItem()
            updateNowPlayingTitle()
        }
        playPauseButton.setOnClickListener {
            if (player.isPlaying) player.pause() else player.play()
            updatePlayPauseLabel()
        }
        findViewById<MaterialButton>(R.id.btnNext).setOnClickListener {
            player.seekToNextMediaItem()
            updateNowPlayingTitle()
        }

        lifecycleScope.launch {
            viewModel.songs.collect { songs ->
                adapter.submitList(songs)
            }
        }

        ensurePermissionAndLoad()
    }

    private fun ensurePermissionAndLoad() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val hasPermission = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            loadSongs()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    private fun loadSongs() {
        lifecycleScope.launch {
            val songs = withContext(Dispatchers.IO) {
                repository.loadSongs()
            }
            viewModel.setSongs(songs)
            player.setMediaItems(songs.map { MediaItem.fromUri(it.contentUri) })
            player.prepare()
        }
    }

    private fun playSongAt(position: Int) {
        player.seekTo(position, 0)
        player.play()
        updateNowPlayingTitle()
        updatePlayPauseLabel()
    }

    private fun updateNowPlayingTitle() {
        val index = player.currentMediaItemIndex
        val songs = viewModel.songs.value
        if (index in songs.indices) {
            titleText.text = songs[index].title
        }
    }

    private fun updatePlayPauseLabel() {
        playPauseButton.text = if (player.isPlaying) getString(R.string.pause) else getString(R.string.play)
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }
}
