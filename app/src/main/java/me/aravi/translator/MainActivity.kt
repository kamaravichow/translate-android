package me.aravi.translator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.*
import me.aravi.translator.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding;
    private lateinit var translator:Translator;
    val modelManager = RemoteModelManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)

        translator = setLanguageOptionsToTranslator(TranslateLanguage.ENGLISH, TranslateLanguage.HINDI)

        checkModelAvailability({
            binding.translateButton.isEnabled = true
            Toast.makeText(applicationContext, "Ready", Toast.LENGTH_LONG).show();
        }, {
            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show();
            binding.translateButton.isEnabled = false
        })

        binding.translateButton.setOnClickListener{
            var result = translator.translate(binding.sourceInput.text.toString()).addOnCompleteListener {
                if (it.isSuccessful){
                    binding.resultText.text = it.result.toString()
                }
            }
        }
    }



    /**
     * Takes languages and builds a translator object
     */
    private fun setLanguageOptionsToTranslator(sourceLang : String, target: String): Translator {
        val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLang)
                .setTargetLanguage(target)
                .build()
        return Translation.getClient(options)
    }


    /**
     * Checks availability and downloads if necessary
     */
    private fun checkModelAvailability(successListener: OnSuccessListener<Void>, failureListener: OnFailureListener){
        val conditions = DownloadConditions.Builder()
                .build()
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener)
    }


    private fun deleteLanguageModel(lang: String){
        // Delete the German model if it's on the device.
        val modelToBeDeleted = TranslateRemoteModel.Builder(lang)
            .build()

        modelManager.deleteDownloadedModel(modelToBeDeleted)
            .addOnSuccessListener {
                // Model deleted.
            }
            .addOnFailureListener {
                // Error.
            }
    }



    fun getDownloadedModels(): MutableSet<TranslateRemoteModel> {
        var list: MutableSet<TranslateRemoteModel> = mutableSetOf()
        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                list = models
            }
            .addOnFailureListener {

            }
        return list
    }




    override fun onDestroy() {
        translator.close();
        super.onDestroy()
    }

}