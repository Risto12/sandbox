package com.example.sandboxapplication

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.example.sandboxapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class FlowingCounter {
    private val _stateFlow = MutableStateFlow("0")
    val stateflow = _stateFlow.asStateFlow()

    suspend fun update(value: String) {
        _stateFlow.emit(value)
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var flowingCounter: FlowingCounter
    var counter: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        flowingCounter = FlowingCounter()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        CoroutineScope(CoroutineName("Emitter")).launch {
            while(true) {
                delay(5000)
                flowingCounter.update("$counter")
                counter += 1
            }
        }
        CoroutineScope(CoroutineName("Never ending collector")).launch {
            flowingCounter.stateflow.collect { println("Counter increased $it") }
        }
    }

}