package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.lazy_loading

import androidx.lifecycle.ViewModel
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.generateData
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class LazyLoadingDemoViewModel : ViewModel() {
    private var dataDisposable: Disposable? = null
    private var dataListener: DataListener? = null
    private var data: MutableList<DemoModel> = mutableListOf()
    private var offset = 0

    init {
        val initialCount = 4
        val initialData = generateData(initialCount)
        offset = initialCount
        data.addAll(initialData)
    }

    fun setListeners(dataListener: DataListener?) {
        this.dataListener = dataListener
    }

    fun getCategories(refresh: Boolean = false) {
        dataDisposable = Single.create { emitter ->
            Thread.sleep(4000)
            emitter.onSuccess(generateData(10, offset))
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result: List<DemoModel> ->
                dataDisposable!!.dispose()
                data.addAll(result)
                offset += result.size
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
        fun onSuccess(categories: List<DemoModel>)
        fun onFailure(errorDetails: String?)
    }
}