package com.crystalist.android.ui.screens

import android.app.Activity
import android.os.Bundle
import com.crystalist.android.utils.CrashHandler

class CrashLogsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(CrashHandler.logsIntent(this))
        finish()
    }
}
