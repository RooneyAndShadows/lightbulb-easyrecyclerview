package com.github.rooneyandshadows.lightbulb.easyrecyclerview.actions

import android.os.Handler
import android.os.Looper
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel
import java.util.concurrent.Executor
import java.util.concurrent.Executors

abstract class AsyncAction<ItemType : EasyAdapterDataModel>(
    private val action: Action<ItemType>,
    private val onCompleteCallback: OnComplete<ItemType>,
    private val onErrorCallback: OnError<ItemType>? = null,
) {
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val currentThread: Thread? = null
    private val pauseLock = Object()
    private var scheduledRecyclerAction = false
    private var recyclerView: EasyRecyclerView<ItemType>? = null
        set(value) {
            if (scheduledRecyclerAction) handler.post { field = value }
            else field = value
        }

    @Volatile
    var isPaused: Boolean = false
        private set

    @Volatile
    var isRunning: Boolean = false
        private set

    open fun beforeExecute(easyRecyclerView: EasyRecyclerView<ItemType>) {
    }

    open fun onComplete(easyRecyclerView: EasyRecyclerView<ItemType>) {
    }

    open fun onError(easyRecyclerView: EasyRecyclerView<ItemType>, exception: java.lang.Exception) {
    }

    fun executeAsync() {
        if (recyclerView == null || isRunning) return
        executor.execute {
            isRunning = true
            try {
                handler.post { beforeExecute(recyclerView!!) }
                action.execute(recyclerView!!).apply {
                    if (waitIfPaused()) return@execute
                    scheduledRecyclerAction = handler.post {
                        isRunning = false
                        onComplete(recyclerView!!)
                        onCompleteCallback.execute(this, recyclerView!!)
                        scheduledRecyclerAction = false
                    }
                }

            } catch (exception: java.lang.Exception) {
                if (waitIfPaused()) return@execute
                scheduledRecyclerAction = handler.post {
                    isRunning = false
                    onError(recyclerView!!, exception)
                    onErrorCallback?.execute(exception, recyclerView!!)
                    scheduledRecyclerAction = false
                }
            }
        }
    }

    fun dispose() {
        currentThread?.interrupt()
        recyclerView = null
    }

    private fun waitIfPaused(): Boolean {
        return try {
            if (!isPaused) return false
            synchronized(pauseLock) {
                pauseLock.wait()
            }
            false
        } catch (exception: InterruptedException) {
            true
        }
    }

    internal fun onAttached(easyRecyclerView: EasyRecyclerView<ItemType>) {
        if (recyclerView == null) recyclerView = easyRecyclerView
        if (!isRunning || !isPaused) return
        synchronized(pauseLock) {
            isPaused = false
            pauseLock.notifyAll()
        }
    }

    internal fun onDetached() {
        if (!isPaused && isRunning)
            synchronized(pauseLock) { isPaused = true }
        recyclerView = null
    }

    interface OnError<ItemType : EasyAdapterDataModel> {
        fun execute(error: java.lang.Exception, easyRecyclerView: EasyRecyclerView<ItemType>)
    }

    interface OnComplete<ItemType : EasyAdapterDataModel> {
        fun execute(result: List<ItemType>, easyRecyclerView: EasyRecyclerView<ItemType>)
    }

    interface Action<ItemType : EasyAdapterDataModel> {
        fun execute(easyRecyclerView: EasyRecyclerView<ItemType>): List<ItemType>
    }
}