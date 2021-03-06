package com.example.bankapplication2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import java.security.MessageDigest
import javax.net.ssl.HttpsURLConnection
import kotlinx.coroutines.*
import org.json.JSONArray
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var button_btn : Button
    private lateinit var id_edittxt : EditText
    private lateinit var masterkey_edittxt : EditText
    private lateinit var key_entered : String
    private lateinit var API : String
    companion object {
        init {
            System.loadLibrary("native-lib")
        }

        @JvmStatic
        external fun baseUrlFromJNI(): String

        @JvmStatic
        external fun Masterkey(): String

        @JvmStatic
        external fun Filename(): String
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_btn = findViewById(R.id.activity_main_convert_btn)
        id_edittxt = findViewById(R.id.activity_main_id_edittxt)
        masterkey_edittxt = findViewById(R.id.activity_main_masterkey_edittxt)

        // on-click listener
        button_btn.setOnClickListener {
            connect_client()
        }
    }

        private fun connect_client()
        {
            try {
                val id = id_edittxt.text.toString()
                 key_entered = masterkey_edittxt.text.toString()
                val hash_masterkey  = PolyDecryption(Masterkey(),key_entered)
                val bytes = key_entered.toByteArray()
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(bytes)
                val hash_entered =  digest.fold("", { str, it -> str + "%02x".format(it) })
                id.toInt()
                // Clé invalide, on discard
                if(hash_entered != hash_masterkey)
                {
                    Toast.makeText(applicationContext,"Oops, something went wrong", Toast.LENGTH_SHORT).show()
                }
                else {

                    API = PolyDecryption(baseUrlFromJNI(), key_entered)
                    val dataJson = GetDataFromApi("config", id.toInt())
                    val intent = Intent(this, AccountActivity::class.java )
                    intent.putExtra("name", dataJson["name"] as String)
                    intent.putExtra("lastname", dataJson["lastname"] as String)
                    intent.putExtra("id", dataJson["id"] as String)
                    intent.putExtra("key_entered", key_entered)
                    startActivity(intent)
                }
            } catch (e:Exception){
                Toast.makeText(applicationContext,"Oops, something went wrong", Toast.LENGTH_SHORT).show()
            }

        }

        private fun GetDataFromApi (query : String, id : Int) : JSONObject {

            try {
                var data_Json: JSONObject
                runBlocking {
                    val job = GlobalScope.async {
                        try {
                            val url = API + query
                            val conn: HttpsURLConnection = URL(url).openConnection() as HttpsURLConnection
                            val responseCode: Int = conn.responseCode
                            var data = conn.inputStream.bufferedReader().readText()
                            data = "{ data :$data}"
                            data_Json = JSONObject(data)
                            data_Json
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    data_Json = job.await() as JSONObject
                }
                Toast.makeText(applicationContext,"Using online data", Toast.LENGTH_SHORT).show()
                val filename = PolyDecryption(Filename(), key_entered)
                val myFile = File(filesDir, filename)
                var data_fichier = data_Json["data"].toString()
                var data_fichier_encrypt = PolyEncryption(data_fichier, key_entered)
                myFile.writeText(data_fichier_encrypt)
                return (data_Json["data"] as JSONArray).getJSONObject(id-1)
            } catch (e : Exception){
                Toast.makeText(applicationContext,"Error, using saved data", Toast.LENGTH_SHORT).show()
                val filename = PolyDecryption(Filename(), key_entered)
                val myFile = File(filesDir, filename)
                var donnee  = myFile.readText()
                donnee = PolyDecryption(donnee, key_entered)
                donnee = "{ data :$donnee}"
                val data_Json = JSONObject(donnee)
                return (data_Json["data"] as JSONArray).getJSONObject(id-1)
            }

        }
    private fun PolyEncryption(to_encrypt: String, key: String): String {
        val encrypted = StringBuilder()
        val size= key.length
        for(i in to_encrypt.indices){
            print(i)
            to_encrypt[i].toChar()
            print(" "+to_encrypt[i]+" ")
            var new_ascii= to_encrypt[i].toInt() + key[i%size].toInt()
            println(new_ascii)
            if(new_ascii>127) new_ascii= 32 + new_ascii%127

            print(new_ascii)
            print(" ")
            encrypted.append(new_ascii.toChar())
            println(encrypted)
        }
        return encrypted.toString()
    }

    private fun PolyDecryption(to_decrypt: String, key: String): String {
        val decrypted = StringBuilder()
        val size= key.length
        for(i in to_decrypt.indices){
            var old_ascii= to_decrypt[i].toInt() - key[i%size].toInt()
            if(old_ascii<32){
                val diff=32-old_ascii
                old_ascii=127-diff
            }
            decrypted.append(old_ascii.toChar())
        }
        return decrypted.toString()
    }


}