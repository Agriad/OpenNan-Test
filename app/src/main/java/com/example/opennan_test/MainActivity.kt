package com.example.opennan_test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.aware.AttachCallback
import android.net.wifi.aware.WifiAwareManager
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.opennan_test.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var wifiAwareSupport = false
    private var wifiAwareAvailable = -1
    private var wifiAwareSession = -1;

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

        checkWifiAwareSupport(this)
        val wifiAwareManager = startWifiAwareBroadcastReceiver(this)
        changeWifiAwareSupportIndicator()
        changeWifiAwareAvailabilityIndicator()

        val buttonPublish = findViewById<Button>(R.id.publish)

        buttonPublish.setOnClickListener {
            if (wifiAwareSession != 1) {
                wifiAwareSession = 1
                val attachCallback = getWifiAwareSession(wifiAwareManager)
                Toast.makeText(this, "Publishing", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Already in a session", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonSubscribe = findViewById<Button>(R.id.subscribe)

        buttonSubscribe.setOnClickListener {
            if (wifiAwareSession != 1) {
                wifiAwareSession = 1
                val attachCallback = getWifiAwareSession(wifiAwareManager)
            } else {
                Toast.makeText(this, "Already in a session", Toast.LENGTH_SHORT).show()
            }
        }
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
        if (wifiAwareAvailable == 1) {
            wifiAwareAvailabilitytext.text = "Wifi Aware is available"
        } else if (wifiAwareAvailable == -1) {
            wifiAwareAvailabilitytext.text = "Wifi Aware app just started"
        } else {
            wifiAwareAvailabilitytext.text = "Wifi Aware is not available"
        }
    }

    private fun startWifiAwareBroadcastReceiver(context: Context): WifiAwareManager {
        val wifiAwareManager =
            context.getSystemService(Context.WIFI_AWARE_SERVICE) as WifiAwareManager
        val filter = IntentFilter(WifiAwareManager.ACTION_WIFI_AWARE_STATE_CHANGED)

        val wifiAwareBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // discard current sessions
                if (wifiAwareManager.isAvailable) {
                    wifiAwareAvailable = 1
                    changeWifiAwareAvailabilityIndicator()
                    Toast.makeText(
                        context,
                        "Wifi Aware available",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    wifiAwareAvailable = 0
                    changeWifiAwareAvailabilityIndicator()
                    Toast.makeText(
                        context,
                        "Wifi Aware not available",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }

        context.registerReceiver(wifiAwareBroadcastReceiver, filter)

        return wifiAwareManager
    }

    private fun getWifiAwareSession(wifiAwareManager: WifiAwareManager): AttachCallback {
        val attachCallback = AttachCallback()
        val handler = Handler()

        wifiAwareManager.attach(attachCallback, handler)

        return attachCallback
    }
}