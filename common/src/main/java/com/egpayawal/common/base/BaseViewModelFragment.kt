package com.egpayawal.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModelStore
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

/**
 * Base fragment class for fragments that has view models.
 *
 * Automatically initializes ViewDataBinding class and ViewModel class for your fragment.
 */
abstract class BaseViewModelFragment<B : ViewDataBinding, VM : BaseViewModel> : BaseFragment<B>() {

    protected val viewModel: VM by createViewModelLazy(
        viewModelClass = getViewModelKClass(),
        storeProducer = { getViewModelStoreProducer() }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel.onCreate(arguments)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getViewModelKClass(): KClass<VM> {
        // Gets the class type passed in VM parameter.
        // https://stackoverflow.com/a/52073780/5285687
        val viewModelClass = (javaClass.genericSuperclass as ParameterizedType)
            .actualTypeArguments[1] as Class<VM>
        return viewModelClass.kotlin
    }

    private fun getViewModelStoreProducer(): ViewModelStore {
        val owner = when {
            setActivityAsViewModelProvider() -> requireActivity()
            setParentFragmentAsViewModelProvider() -> requireParentFragment()
            else -> this
        }
        return owner.viewModelStore
    }

    open fun setParentFragmentAsViewModelProvider(): Boolean = false

    open fun setActivityAsViewModelProvider(): Boolean = false
}
