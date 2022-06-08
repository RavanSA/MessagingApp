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

    // Filename for the exported vocab ( .json )
    private var filename : String? = jsonFilename

    // Max length of the input sequence for the given model.
    private var maxlen : Int = inputMaxLen

    private var vocabData : HashMap< String , Int >? = null

    // Load the contents of the vocab ( see assets/word_dict.json )
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

    // Tokenize the given sentence
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
                Log.d("TOKENIZEDMESSAGEINDEZ", index.toString())
                Log.d("TOKENIZEDMESSAGE", tokenizedMessage.toString())
            }
        }
        return tokenizedMessage.toFloatArray()
    }

    // Pad the given sequence to maxlen with zeros.
    fun padSequence ( sequence : FloatArray ) : FloatArray {
        val maxlen = this.maxlen
        Log.d("MAXLEN", maxlen.toString())
        if (sequence.size > maxlen) {
            Log.d("SEQUNCE1IF", sequence.toString())
            return sequence.sliceArray( 0..maxlen )
        }
        else if ( sequence.size < maxlen ) {
            Log.d("ARRAY 0", sequence.size.toString())
            val array = ArrayList<Float>()
//            for (element in sequence){
//                array.add(element)
//            }
//            Log.d("")
//            val array = FloatArray(300) { sequence[it].toFloat() }
            Log.d("ARRAYSIZE", array.toString())
            val rangeArrSize = maxlen-sequence.size
            Log.d("RANGERARRSIZE", rangeArrSize.toString())
            for ( i in 0..rangeArrSize-1){
                Log.d("IIIIIIII", i.toString())
                array.add(i,0F)
            }

            for(rangeArrSize in rangeArrSize..maxlen-1){
//                array[rangeArrSize] = sequence[maxlen-rangeArrSize]
                Log.d("RANNGEARRINNMAXLEN", rangeArrSize.toString())
                Log.d("SEQUENCEARRSIZE", (maxlen-rangeArrSize-1).toString())
                Log.d("SEQUENCEVALUE", sequence[maxlen-rangeArrSize-1].toString())
                array.add(rangeArrSize,sequence[maxlen-rangeArrSize-1].toFloat())
            }

            Log.d("SEQUENCE2IF", sequence.toString())
            return array.toFloatArray()
        } else{
            Log.d("SEQUENCE3IF", sequence.toString())
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