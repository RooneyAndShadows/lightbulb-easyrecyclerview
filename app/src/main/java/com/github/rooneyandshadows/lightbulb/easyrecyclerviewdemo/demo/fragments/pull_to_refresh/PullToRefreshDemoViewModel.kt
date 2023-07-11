package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.pull_to_refresh

import androidx.lifecycle.ViewModel
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateData
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers


class PullToRefreshDemoViewModel : ViewModel() {
    private var dataDisposable: Disposable? = null
    private var dataListener: DataListener? = null
    private var data: MutableList<DemoModel> = mutableListOf()
    val listData: List<DemoModel>
        get() = data.toList()

    init {
        val initialCount = 4
        val initialData = generateData(initialCount)
        data.addAll(initialData)
    }

    fun setListeners(dataListener: DataListener?) {
        this.dataListener = dataListener
    }

    fun refreshData() {
        dataDisposable = Single.create { emitter ->
            Thread.sleep(4000)
            emitter.onSuccess(generateData(10))
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result: List<DemoModel> ->
                dataDisposable!!.dispose()
                data.clear()
                data.addAll(result)
                dataListener?.onSuccess(result)
            }) { throwable: Throwable ->
                dataDisposable!!.dispose()
                dataListener?.onFailure(throwable.message)
            }
    }

    @Override
    override fun onCleared() {
        super.onCleared()
        dataDisposable?.dispose()
    }

    interface DataListener {
        fun onSuccess(items: List<DemoModel>)
        fun onFailure(errorDetails: String?)
    }
}