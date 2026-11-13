package com.kudoryi.physec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kudoryi.physec.data.PhySecRepository
import com.kudoryi.physec.ui.compose.PhySecApp
import com.kudoryi.physec.ui.theme.PhySecTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PhySecRepository.init(applicationContext)

        setContent {
            PhySecTheme {
                PhySecApp()
            }
        }
    }
}
