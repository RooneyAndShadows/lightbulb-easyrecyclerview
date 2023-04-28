package com.github.rooneyandshadows.lightbulb.easyrecyclerview.actions

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.github.rooneyandshadows.lightbulb.easyrecyclerview.EasyRecyclerView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel
import java.util.concurrent.Executor
import java.util.concurrent.Executors

abstract class AsyncAction<ItemType : EasyAdapterDataModel>(
    private val action: Action<ItemType>,
    private val onCompleteCallback: OnComplete<ItemType>,
    private val onErrorCallback: OnError<ItemType>? = null,
    lifecycleOwner: LifecycleOwner? = null
) : DefaultLifecycleObserver {
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val currentThread: Thread? = null
    private val pauseLock = Object()

    @Volatile
    private var easyRecyclerView: EasyRecyclerView<ItemType>? = null

    @Volatile
    var isPaused: Boolean = false
        private set

    @Volatile
    var isRunning: Boolean = false
        private set

    init {
        lifecycleOwner?.apply {
            lifecycle.removeObserver(this@AsyncAction)
            lifecycle.addObserver(this@AsyncAction)
        }
    }

    @Override
    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        easyRecyclerView = null
        pauseThread()
    }

    open fun beforeExecute(easyRecyclerView: EasyRecyclerView<ItemType>) {
    }

    open fun onComplete(easyRecyclerView: EasyRecyclerView<ItemType>) {
    }

    open fun onError(easyRecyclerView: EasyRecyclerView<ItemType>, exception: java.lang.Exception) {
    }

    internal fun attachTo(easyRecyclerView: EasyRecyclerView<ItemType>) {
        this.easyRecyclerView = easyRecyclerView
        resumeThread()
    }

    internal fun detachFromRecycler() {
        this.easyRecyclerView = null
        dispose()
    }

    fun executeAsync() {
        if (easyRecyclerView == null || isRunning) return
        executor.execute {
            isRunning = true
            try {
                handler.post { beforeExecute(easyRecyclerView!!) }
                action.execute(easyRecyclerView!!).apply {
                    waitIfPaused()
                    handler.post {
                        isRunning = false
                        onComplete(easyRecyclerView!!)
                        onCompleteCallback.execute(this, easyRecyclerView!!)
                    }
                }

            } catch (exception: java.lang.Exception) {
                waitIfPaused()
                handler.post {
                    isRunning = false
                    onError(easyRecyclerView!!, exception)
                    onErrorCallback?.execute(exception, easyRecyclerView!!)
                }
            }
        }
    }

    fun dispose() {
        currentThread?.interrupt()
    }

    private fun waitIfPaused() {
        try {
            if (isPaused) {
                synchronized(pauseLock) {
                    pauseLock.wait()
                }
            }
        } catch (exception: InterruptedException) {
            //ignore
        }
    }

    private fun pauseThread() {
        if (!isPaused && isRunning)
            synchronized(pauseLock) {
                isPaused = true
            }
    }

    private fun resumeThread() {
        if (!isRunning || !isPaused) return
        synchronized(pauseLock) {
            isPaused = false
            pauseLock.notifyAll()
        }
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