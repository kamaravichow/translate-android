package me.aravi.translator

import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateRemoteModel

class ModelManager {

    val modelManager = RemoteModelManager.getInstance()

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



}