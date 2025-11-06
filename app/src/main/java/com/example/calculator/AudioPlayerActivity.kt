package com.example.calculator

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import android.media.MediaMetadataRetriever

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var timeBar: SeekBar
    private lateinit var soundBar: SeekBar
    private lateinit var currentTimeText: TextView
    private lateinit var totalTimeText: TextView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var folderButton: ImageView
    private lateinit var authorText: TextView
    private lateinit var trackImage: ImageView
    private lateinit var buttonPlayPause: ImageView
    private lateinit var nextButton: ImageView
    private lateinit var previosButton: ImageView
    private lateinit var buttonFavorite: ImageView
    private lateinit var buttonBackground: ImageView
    private var isPlaying = false
    private var isFavorite = false
    private var isBackground = true
    private val musicList: MutableList<Uri> = mutableListOf()
    private var currentIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)

        timeBar = findViewById(R.id.time_bar)
        soundBar = findViewById(R.id.volume_bar)
        currentTimeText = findViewById(R.id.cur_time)
        totalTimeText = findViewById(R.id.time_of_music)
        folderButton = findViewById(R.id.button_files)
        buttonPlayPause = findViewById(R.id.button_play)
        buttonFavorite = findViewById(R.id.button_like)
        buttonBackground = findViewById(R.id.button_background)
        nextButton = findViewById(R.id.button_next)
        previosButton = findViewById(R.id.button_prev)
        authorText = findViewById(R.id.author)
        trackImage = findViewById(R.id.preview)

        mediaPlayer = MediaPlayer()

        folderButton.setOnClickListener {
            SearchFolder()
        }

        buttonFavorite.setOnClickListener {
            isFavorite = !isFavorite
            val image = if (isFavorite) R.drawable.like_filled else R.drawable.like
            buttonFavorite.setImageResource(image)
        }

        buttonBackground.setOnClickListener {
            isBackground = !isBackground
            val image = if (isBackground) R.drawable.button_music_yes else R.drawable.button_music_no
            buttonBackground.setImageResource(image)
        }

        buttonPlayPause.setOnClickListener {
            isPlaying = !isPlaying
            if (isPlaying) {
                buttonPlayPause.setImageResource(R.drawable.button_pause)
                mediaPlayer.start()
            } else {
                buttonPlayPause.setImageResource(R.drawable.button_play)
                mediaPlayer.pause()
            }
        }

        nextButton.setOnClickListener {
            switchTrack(1)
        }

        previosButton.setOnClickListener {
            switchTrack(-1)
        }

        soundBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?,progress: Int,fromUser: Boolean){
                if(fromUser){
                    val cur = progress.toFloat()/100
                    mediaPlayer.setVolume(cur,cur)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        timeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

    }

    private val PICK_FOLDER_REQUEST_CODE = 42

    private fun SearchFolder() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, PICK_FOLDER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FOLDER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            LoadMusicFromFolder(uri)
        }
    }

    fun LoadMusicFromFolder(folderUri: Uri) {
        val folder = DocumentFile.fromTreeUri(this, folderUri)

        musicList.clear()

        if (folder == null || !folder.isDirectory) {
            Toast.makeText(this, "Выберите папку.", Toast.LENGTH_SHORT).show()
            return
        }

        folder.listFiles().forEach { file ->
            if (file.isFile) {
                if (file.isFile && (file.name?.endsWith(".mp3") == true) ) {
                    musicList.add(file.uri)
                }
            }
        }

        if (musicList.isEmpty()) {
            Toast.makeText(this, "В папке нет файлов", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Загружено файлов: ${musicList.size}", Toast.LENGTH_SHORT).show()
        currentIndex = 0
        PlaySong(currentIndex)
    }

    fun PlaySong(index: Int) {
        if (musicList.isEmpty()) return

        stopSeekBarUpdate()
        mediaPlayer.reset()
        mediaPlayer.setDataSource(this, musicList[index])
        mediaPlayer.prepare()
        mediaPlayer.start()
        isPlaying = true
        buttonPlayPause.setImageResource(R.drawable.button_pause)
        startSeekBarUpdate()

        mediaPlayer.setOnCompletionListener {
            switchTrack(1)
        }

        mediaPlayer.setOnPreparedListener {
            val duration = mediaPlayer.duration
            timeBar.max = duration
            totalTimeText.text = "${"%02d".format((duration / 1000 / 60))}:${"%02d".format((duration / 1000 % 60))}"
            currentTimeText.text = ("00:00")
        }
        val uri = musicList[index]
        val fileName = DocumentFile.fromSingleUri(this, uri)?.name ?: "Неизвестный трек"
        val trackName = fileName.substringBeforeLast(".")
        val (artist, bitmap) = getTrackInfo(uri)
        authorText.text = artist ?: "Неизвестный исполнитель"
        if (bitmap != null) {
            trackImage.setImageBitmap(bitmap)
        } else {
            trackImage.setImageResource(R.drawable.placeholder)
        }
        findViewById<TextView>(R.id.cur_track).text = trackName
    }

    private val handler = android.os.Handler()
    private val updateSeekBar = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                val currentPosition = mediaPlayer.currentPosition
                val duration = mediaPlayer.duration
                timeBar.progress = currentPosition
                timeBar.max = duration
                currentTimeText.text = "${"%02d".format((currentPosition / 1000 / 60))}:${"%02d".format((currentPosition / 1000 % 60))}"
            }
            handler.postDelayed(this, 500)
        }
    }

    private fun startSeekBarUpdate() {
        handler.post(updateSeekBar)
    }

    private fun stopSeekBarUpdate() {
        handler.removeCallbacks(updateSeekBar)
    }

    fun getTrackInfo(uri: Uri): Pair<String?, android.graphics.Bitmap?> {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(applicationContext, uri)

        val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        val imageBytes = retriever.embeddedPicture
        val bitmap = imageBytes?.let { android.graphics.BitmapFactory.decodeByteArray(it, 0, it.size) }

        return Pair(artist, bitmap)
    }

    private fun switchTrack(direction: Int) {
        if (musicList.isEmpty()) return

        currentIndex += direction

        if (currentIndex >= musicList.size) {
            currentIndex = 0
        } else if (currentIndex < 0) {
            currentIndex = musicList.size - 1
        }

        PlaySong(currentIndex)
    }

    override fun onPause()
    {
        super.onPause()
        if (mediaPlayer.isPlaying and !isBackground)
        {
            mediaPlayer.pause()
            isPlaying = false
            buttonPlayPause.setImageResource(R.drawable.button_play)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSeekBarUpdate()
        mediaPlayer.release()
    }
}