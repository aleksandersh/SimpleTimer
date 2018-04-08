package io.github.aleksandersh.simpletimer.presentation.util

import android.content.Context
import android.support.annotation.PluralsRes
import android.support.annotation.StringRes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceManager
@Inject
constructor(private val context: Context) {

    fun getString(@StringRes resId: Int): String {
        return context.resources.getString(resId)
    }

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return context.resources.getString(resId, *formatArgs)
    }

    fun getQuantityString(@PluralsRes resId: Int, quantity: Int): String {
        return context.resources.getQuantityString(resId, quantity)
    }

    fun getQuantityString(@PluralsRes resId: Int, quantity: Int, vararg formatArgs: Any): String {
        return context.resources.getQuantityString(resId, quantity, *formatArgs)
    }
}