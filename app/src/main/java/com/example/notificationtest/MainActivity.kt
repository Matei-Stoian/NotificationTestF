package com.example.notificationtest

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.notificationtest.ui.theme.NotificationtestTheme
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.lang.StringBuilder

class MainActivity : ComponentActivity() {
    private lateinit var csvContentTextView: TextView
    private val csvFilePath = getExternalFilesDir(null).toString() + "/notification_log.csv"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_csv)
        csvContentTextView = findViewById(R.id.csvContentTextView)
        val csvContent = readCSVFile(csvFilePath)
        csvContentTextView.text = csvContent
        if(!isNoificationServiceEnable())
        {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivityForResult(intent,123)
        } else{
            startNotificationService()
        }
    }
    private fun readCSVFile(FilePath:String):String{
        val content = StringBuilder()
        try{
            BufferedReader(FileReader(FilePath)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    content.append(line).append("\n")
                }
            }
        } catch (e: IOException){
            e.printStackTrace()
        }
        return content.toString()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123){
            if(isNoificationServiceEnable())
            {
                startNotificationService()
            }else{
                finishAffinity()
            }
        }
    }
    private fun isNoificationServiceEnable(): Boolean{
        val cn = ComponentName(this, ListenClosely::class.java)
        var enabledListners = Settings.Secure.getString(contentResolver,"enabled_notification_listeners")
        return enabledListners?.contains(cn.flattenToString()) == true
    }
    private fun startNotificationService(){
        startService(Intent(this,ListenClosely::class.java))
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NotificationtestTheme {
        Greeting("Android")
    }
}