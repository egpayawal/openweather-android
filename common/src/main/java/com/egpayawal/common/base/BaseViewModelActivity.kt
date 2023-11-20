package com.egpayawal.common.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelLazy
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

/**
 * Automatically initializes ViewDataBinding class and ViewModel class for your activity.
 */
abstract class BaseViewModelActivity<B : ViewDataBinding, VM : BaseViewModel> : BaseActivity<B>() {

    protected val viewModel: VM by viewModels(getViewModelKClass())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onCreate(intent.extras)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getViewModelKClass(): KClass<VM> {
        // Gets the class type passed in VM parameter.
        // https://stackoverflow.com/a/52073780/5285687
        val viewModelClass = (javaClass.genericSuperclass as ParameterizedType)
            .actualTypeArguments[1] as Class<VM>
        return viewModelClass.kotlin
    }

    @MainThread
    private fun ComponentActivity.viewModels(viewModelClass: KClass<VM>): Lazy<VM> {
        return ViewModelLazy(
            viewModelClass = viewModelClass,
            storeProducer = { viewModelStore },
            factoryProducer = { defaultViewModelProviderFactory }
        )
    }
}
