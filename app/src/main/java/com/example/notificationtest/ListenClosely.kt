package com.example.notificationtest

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import com.opencsv.CSVWriter
import java.io.FileWriter
import java.io.IOException

class ListenClosely : NotificationListenerService() {

    private lateinit var CsvWriter: CSVWriter
    private val csvHeader = arrayOf("Package", "Title","Text","TimeStamp")

    override fun onCreate() {
        super.onCreate()
        initCSV()
    }
    private fun initCSV()
    {
        try{
            val csvFilePath = getExternalFilesDir(null).toString() + "/notification_log.csv"
            CsvWriter = CSVWriter(FileWriter(csvFilePath,true))
            CsvWriter.writeNext(csvHeader)
            CsvWriter.flush()
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CloseCSV()
    }
    private fun CloseCSV(){
        if(::CsvWriter.isInitialized){
            try{
                CsvWriter.close()
            } catch (e: IOException)
            {
                e.printStackTrace()
            }
        }
    }
    private fun logNotification(packageName:String, title:String?,text:String?){
        val Data = arrayOf(packageName,title ?:"",text ?:"")
        CsvWriter.writeNext(Data)
        CsvWriter.flush()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notification = sbn.notification
        val packageName = sbn.packageName
        val title = notification?.extras?.getString(NotificationCompat.EXTRA_TITLE)
        val text = notification?.extras?.getString(NotificationCompat.EXTRA_TEXT)
        logNotification(packageName,title,text)
        Log.d("Notification Listner", "Notification Posted: Package: $packageName,Title: $title,Text: $text")
    }
}