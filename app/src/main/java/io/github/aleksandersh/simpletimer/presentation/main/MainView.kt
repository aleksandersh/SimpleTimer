package io.github.aleksandersh.simpletimer.presentation.main

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface MainView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setClickCountText(text: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setTimerText(text: String)

    fun startTimerService()

    fun stopTimerService()

    fun bindTimerService()

    fun unbindTimerService()

    fun startTimer(time: Long)

    fun restartTimer(time: Long)

    fun addTime(time: Long)

    fun finishApplication()

    fun showFinishDialog()
}