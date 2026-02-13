package com.example.localmusicplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongAdapter(
    private var songs: List<Song>,
    private val onSongClicked: (Int) -> Unit,
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    fun submitList(newSongs: List<Song>) {
        songs = newSongs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun getItemCount(): Int = songs.size

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
        holder.itemView.setOnClickListener { onSongClicked(position) }
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.songTitle)
        private val subtitle: TextView = itemView.findViewById(R.id.songSubtitle)

        fun bind(song: Song) {
            title.text = song.title
            subtitle.text = "${song.artist} • ${song.durationMs / 1000}s"
        }
    }
}
