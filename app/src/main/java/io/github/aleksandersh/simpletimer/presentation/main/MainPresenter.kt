package io.github.aleksandersh.simpletimer.presentation.main

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.github.aleksandersh.simpletimer.R
import io.github.aleksandersh.simpletimer.presentation.util.ResourceManager

@InjectViewState
class MainPresenter(
    private val resourceManager: ResourceManager
) : MvpPresenter<MainView>() {

    companion object {

        private const val START_TIME_SEC = 20
        private const val ADDITIONAL_TIME_SEC = 5
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.startTimerService()
    }

    fun onResume() {
        viewState.bindTimerService()
    }

    fun onPause() {
        viewState.unbindTimerService()
    }

    fun onServiceConnected() {
        viewState.startTimer(START_TIME_SEC)
    }

    fun onClickAddTime(clickCount: Int) {
        resourceManager
            .getQuantityString(R.plurals.main_click_count, clickCount, clickCount)
            .let(viewState::setClickCountText)
        viewState.addTime(ADDITIONAL_TIME_SEC)
    }

    fun onClickRetry() {
        viewState.restartTimer(START_TIME_SEC)
    }

    fun onClickFinish() {
        viewState.stopTimerService()
        viewState.finishApplication()
    }

    fun onNextTimerTick(tick: Int) {
        handleTime(tick)
    }

    private fun handleTime(time: Int) {
        showTime(time)
        if (time <= 0) {
            showFinishDialog()
        }
    }

    private fun showTime(time: Int) {
        viewState.setTimerText(time.toString())
    }

    private fun showFinishDialog() {
        viewState.showFinishDialog()
    }
}