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

    @Volatile
    var isRunning: Boolean = false
        private set

    interface OnError<ItemType : EasyAdapterDataModel> {
        fun execute(error: java.lang.Exception, easyRecyclerView: EasyRecyclerView<ItemType>)
    }

    interface OnComplete<ItemType : EasyAdapterDataModel> {
        fun execute(result: List<ItemType>, easyRecyclerView: EasyRecyclerView<ItemType>)
    }

    interface Action<ItemType : EasyAdapterDataModel> {
        fun execute(easyRecyclerView: EasyRecyclerView<ItemType>): List<ItemType>
    }

    open fun beforeExecute(easyRecyclerView: EasyRecyclerView<ItemType>) {
    }

    open fun onComplete(easyRecyclerView: EasyRecyclerView<ItemType>) {
    }

    open fun onError(easyRecyclerView: EasyRecyclerView<ItemType>, exception: java.lang.Exception) {
    }

    fun executeAsync(easyRecyclerView: EasyRecyclerView<ItemType>) {
        if (isRunning) return
        executor.execute {
            try {
                isRunning = true
                handler.post { beforeExecute(easyRecyclerView) }
                action.execute(easyRecyclerView).apply {
                    handler.post {
                        isRunning = false
                        onComplete(easyRecyclerView)
                        onCompleteCallback.execute(this, easyRecyclerView)
                    }
                }
            } catch (exception: java.lang.Exception) {
                handler.post {
                    isRunning = false
                    onError(easyRecyclerView, exception)
                    onErrorCallback?.execute(exception, easyRecyclerView)
                }
            }
        }
    }
}