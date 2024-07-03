package com.example.imagetotextconverter

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.content.ClipboardManager
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainActivity : AppCompatActivity() {

    lateinit var seekBar: SeekBar
    lateinit var textView : EditText
    lateinit var test : TextView
    lateinit var pick : CardView
    lateinit var copy : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        seekBar = findViewById(R.id.size_seekbar)
        textView  = findViewById(R.id.text)
        pick = findViewById(R.id.gallery)
        copy = findViewById(R.id.imageView)


        seekBar.progress = 18
        seekBar.progressDrawable.setColorFilter(ContextCompat.getColor(this,R.color.dark_blue),PorterDuff.Mode.SRC_IN)
        seekBar.thumb.setColorFilter(ContextCompat.getColor(this,R.color.dark_blue),PorterDuff.Mode.SRC_IN)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textView.textSize = progress.toFloat()

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        pick.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        copy.setOnClickListener{
            if(textView.text != null) copyToClipBoard(textView.text.toString())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data!!
            processTheImage(uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Snackbar.make(findViewById<View>(android.R.id.content),"Error",Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(findViewById<View>(android.R.id.content),"canceled!!",Snackbar.LENGTH_SHORT).show()
        }
    }

    fun processTheImage(uri : Uri){
        val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uri)
        val image = InputImage.fromBitmap(bitmap,0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        try{
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    textView.setText(visionText.text)
                }
                .addOnFailureListener{e ->
                    e.printStackTrace()
                }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    fun copyToClipBoard(text : String){
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Recognized Text",text)
        clipboardManager.setPrimaryClip(clip)
        Snackbar.make(findViewById<View>(android.R.id.content),"copied!!",Snackbar.LENGTH_SHORT).show()
    }
}