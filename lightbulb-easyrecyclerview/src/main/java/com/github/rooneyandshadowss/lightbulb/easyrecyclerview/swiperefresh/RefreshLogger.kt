package com.github.rooneyandshadowss.lightbulb.easyrecyclerview.swiperefresh

import android.util.Log

@Suppress("unused")
object RefreshLogger {
    private const val TAG = "RefreshLayout"
    private var mEnableDebug = false
    fun setEnableDebug(enableDebug: Boolean) {
        mEnableDebug = enableDebug
    }

    fun i(msg: String?) {
        if (mEnableDebug) {
            Log.i(TAG, msg!!)
        }
    }

    fun v(msg: String?) {
        if (mEnableDebug) {
            Log.v(TAG, msg!!)
        }
    }

    fun d(msg: String?) {
        if (mEnableDebug) {
            Log.d(TAG, msg!!)
        }
    }

    fun w(msg: String?) {
        if (mEnableDebug) {
            Log.w(TAG, msg!!)
        }
    }

    fun e(msg: String?) {
        if (mEnableDebug) {
            Log.e(TAG, msg!!)
        }
    }
}