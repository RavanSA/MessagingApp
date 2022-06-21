package com.project.messagingapp.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Classifier(context: Context, jsonFilename: String , inputMaxLen : Int ) {

    private var context : Context? = context

    private var filename : String? = jsonFilename

    private var maxlen : Int = inputMaxLen

    private var vocabData : HashMap< String , Int >? = null

    private fun loadJSONFromAsset(filename : String? ): String? {
        var json: String?
        try {
            val inputStream = filename?.let { context!!.assets.open(it) }
            val size = inputStream?.available()
            val buffer = size?.let { ByteArray(it) }
            inputStream?.read(buffer)
            inputStream?.close()
            json = buffer?.let { String(it) }
        }
        catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun processVocab( callback: VocabCallback ) {
        CoroutineScope( Dispatchers.Main ).launch {
            loadVocab( callback , loadJSONFromAsset( filename )!! )
        }
    }

    fun tokenize ( message : String ): FloatArray {

        val parts : List<String> = message.split(" " )
        val tokenizedMessage = ArrayList<Float>()
        for ( part in parts ) {
            if (part.trim() != ""){
                var index : Float? = 0F
                index = if ( vocabData!![part] == null ) {
                    0F
                } else{
                    (vocabData!![part]?.toString() + "F").toFloat()
                }
                tokenizedMessage.add(index!!)
            }
        }
        return tokenizedMessage.toFloatArray()
    }

    fun padSequence ( sequence : FloatArray ) : FloatArray {
        val maxlen = this.maxlen
        Log.d("MAXLEN", maxlen.toString())
        if (sequence.size > maxlen) {
            Log.d("SEQUNCE1IF", sequence.toString())
            return sequence.sliceArray( 0..maxlen )
        }
        else if ( sequence.size < maxlen ) {
            val array = ArrayList<Float>()
            val rangeArrSize = maxlen-sequence.size
            for ( i in 0..rangeArrSize-1){
                array.add(i,0F)
            }

            for(rangeArrSize in rangeArrSize..maxlen-1){
                array.add(rangeArrSize,sequence[maxlen-rangeArrSize-1].toFloat())
            }

            return array.toFloatArray()
        } else{
            return sequence
        }
    }


    interface VocabCallback {
        fun onVocabProcessed()
    }

    private fun loadVocab(callback : VocabCallback, json : String )  {
        with( Dispatchers.Default ) {
            val jsonObject = JSONObject( json )
            val iterator : Iterator<String> = jsonObject.keys()
            val data = HashMap< String , Int >()
            while ( iterator.hasNext() ) {
                val key = iterator.next()
                data[key] = jsonObject.get( key ) as Int
            }
            with( Dispatchers.Main ){
                vocabData = data
                callback.onVocabProcessed()
            }
        }
    }
}