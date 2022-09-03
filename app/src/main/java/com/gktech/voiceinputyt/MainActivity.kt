package com.gktech.voiceinputyt

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.gktech.voiceinputyt.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding:ActivityMainBinding
    get() = _binding!!

    private val allPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        it?.let {
            if (it){
                Toast.makeText(applicationContext,"Permission Granted",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val speechRecognizer:SpeechRecognizer by lazy {
        SpeechRecognizer.createSpeechRecognizer(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnListen.setOnTouchListener { view, motionEvent ->
            when(motionEvent.action){
                MotionEvent.ACTION_UP->{
                    speechRecognizer.stopListening()
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_DOWN->{
                    getPermissionOverO(this){
                        startListen()
                    }
                    return@setOnTouchListener true
                }
                else->{
                    return@setOnTouchListener true
                }
            }
        }

    }

    fun startListen(){
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())
        speechRecognizer.setRecognitionListener(object : RecognitionListener{
            override fun onReadyForSpeech(params: Bundle?) {

            }

            override fun onBeginningOfSpeech() {
               binding.edVoiceInput.setText("Listing...")
            }

            override fun onRmsChanged(rmsdB: Float) {

            }

            override fun onBufferReceived(buffer: ByteArray?) {

            }

            override fun onEndOfSpeech() {

            }

            override fun onError(error: Int) {

            }

            override fun onResults(results: Bundle?) {
                results?.let {
                    val res =it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    binding.edVoiceInput.setText(res?.get(0))
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {

            }

            override fun onEvent(eventType: Int, params: Bundle?) {

            }

        })
        speechRecognizer.startListening(intent)
    }

    fun getPermissionOverO(context: Context,call:()->Unit){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            if (ActivityCompat.checkSelfPermission(context,android.Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED){
                call.invoke()
            }else{
                allPermission.launch(android.Manifest.permission.RECORD_AUDIO)
            }
        }
    }
}