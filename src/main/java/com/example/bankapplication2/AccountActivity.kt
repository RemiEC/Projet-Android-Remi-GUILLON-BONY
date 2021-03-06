package com.example.bankapplication2

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class AccountActivity : AppCompatActivity() {

    private lateinit var name_txt : TextView
    private lateinit var lastname_txt : TextView
    private lateinit var ID_txt : TextView
    private lateinit var listView : ListView
    private  lateinit var refresh_btn : Button
    private lateinit var key_entered : String
    private lateinit var API : String

    companion object {
        init {
            System.loadLibrary("native-lib")
        }

        @JvmStatic
        external fun baseUrlFromJNI(): String

        @JvmStatic
        external fun Filename(): String
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        name_txt = findViewById(R.id.account_name_txt)
        lastname_txt = findViewById(R.id.account_lastname_txt)
        ID_txt = findViewById(R.id.account_id_txt)
        listView = findViewById<ListView>(R.id.recipe_list_view)
        refresh_btn = findViewById(R.id.account_refresh_btn)

        val name = intent.extras!!.get("name") as String
        name_txt.text = "Name :  $name"
        val lastname = intent.extras!!.get("lastname") as String
        lastname_txt.text = "Lastname : $lastname"
        val id = intent.extras!!.get("id") as String
        ID_txt.text = "ID : $id"
        key_entered = intent.extras!!.get("key_entered") as String
        API = PolyDecryption(baseUrlFromJNI(), key_entered)
        Refresh()

        refresh_btn.setOnClickListener {
            Refresh()
        }
    }

    private fun Refresh() {
        val dataJson = GetDataFromApi_Accounts("accounts")
        val listItems = mutableListOf<String>()
        for (i in 0 until (dataJson["data"] as JSONArray).length() ){
            val data_JSON_user= (dataJson["data"] as JSONArray).getJSONObject(i)
            val id = data_JSON_user["id"]
            val accountName = data_JSON_user["accountName"]
            val amount = data_JSON_user["amount"]
            val iban = data_JSON_user["iban"]
            val currency = data_JSON_user["currency"]
            val String_final = "Account ID : $id \n \n accountname : $accountName \n amount : $amount \n iban : $iban \n currency : $currency \n \n"
            listItems.add(String_final)
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter
    }

    private fun GetDataFromApi_Accounts(query: String) : JSONObject {

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
            val data_fichier_encrypt = PolyEncryption(data_Json.toString(), key_entered)
            myFile.writeText(data_fichier_encrypt)
            return data_Json
        } catch (e:Exception){
            Toast.makeText(applicationContext,"No wifi, using saved data", Toast.LENGTH_SHORT).show()
            val filename = PolyDecryption(Filename(), key_entered)
            val myFile = File(filesDir, filename)
            var data  = myFile.readText()
            data = PolyDecryption(data, key_entered)
            val data_JSON = JSONObject(data)
            return data_JSON
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