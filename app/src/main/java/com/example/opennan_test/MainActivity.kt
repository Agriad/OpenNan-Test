package com.example.opennan_test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.aware.WifiAwareManager
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.example.opennan_test.databinding.ActivityMainBinding
import org.w3c.dom.Text
import java.security.Provider

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var wifiAwareSupport = false
    private var wifiAwareAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            changeWifiAwareSupportIndicator()
            changeWifiAwareAvailabilityIndicator()
        }

        var wifiAwareManager =
            this.getSystemService(Context.WIFI_AWARE_SERVICE) as WifiAwareManager?
        var filter = IntentFilter(WifiAwareManager.ACTION_WIFI_AWARE_STATE_CHANGED)

        val wifiAwareBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // discard current sessions
                if (wifiAwareManager?.isAvailable == true) {
                    wifiAwareAvailable = true
                } else {
                    wifiAwareAvailable = false
                }
            }
        }

        this.registerReceiver(wifiAwareBroadcastReceiver, filter)
        checkWifiAwareSupport(this)
        changeWifiAwareSupportIndicator()
        changeWifiAwareAvailabilityIndicator()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun checkWifiAwareSupport(context: Context) {
        wifiAwareSupport =
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)
    }

    private fun changeWifiAwareSupportIndicator() {
        val wifiAwareSupportText: TextView = findViewById(R.id.wifi_aware_support)
        if (wifiAwareSupport) {
            wifiAwareSupportText.text = "This device supports wifi aware"
        } else {
            wifiAwareSupportText.text = "This device does not support wifi aware"
        }
    }

    private fun changeWifiAwareAvailabilityIndicator() {
        val wifiAwareAvailabilitytext: TextView = findViewById(R.id.wifi_aware_availability)
        if (wifiAwareAvailable) {
            wifiAwareAvailabilitytext.text = "Wifi Aware is available"
        } else {
            wifiAwareAvailabilitytext.text = "Wifi Aware is not available"
        }
    }
}