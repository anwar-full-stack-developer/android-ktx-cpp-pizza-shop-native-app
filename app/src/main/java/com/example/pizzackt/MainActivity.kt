package com.example.pizzackt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.pizzackt.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
//            val currentFragment = supportFragmentManager.fragments.last()
            if (navController.currentDestination.toString().contains(".PizzaListFragment") ){
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, PizzaEditFragment.newInstance(
                        actionType = "NEW",
                        position = -1
                    ))
                    .commit()
            } else {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .setAnchorView(R.id.fab).show()
            }
        }

        // Example of a call to a native method
//        binding.sampleText.text = stringFromJNI()

        Log.d("Main", "String From JNI "
                + stringFromJNI())
        Log.d("Main", "Boolean From JNI "
                + boolFromJNI())

       val tmpuser = objectFromJNI(TempUser("87678", "Test Temp User"))
        Log.d("Main", "Object From User: "
                + tmpuser.name.toString())

//       val tmpuser2 = httpGetRequestFromJNI(TempUser("87678-6876", "User2"))
//        Log.d("Main", "Calling HTTP GET Request: "
//                + tmpuser.name.toString())
    }

    /**
     *
     * A native method that is implemented by the 'pizzackt' native library,
     * which is packaged with this application.
     */
    private external fun stringFromJNI(): String
    external fun boolFromJNI(): Boolean
    external fun numberFromJNI(): Long
    external fun objectFromJNI(tempUser: TempUser): TempUser
    external fun httpGetRequestFromJNI(tempUser: TempUser): TempUser

    companion object {
        // Used to load the 'pizzackt' library on application startup.
        init {
            System.loadLibrary("pizzackt")
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

}