package io.github.aleksandersh.simpletimer.presentation.main

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.github.aleksandersh.simpletimer.R
import io.github.aleksandersh.simpletimer.data.TimerRepository
import io.github.aleksandersh.simpletimer.presentation.util.ResourceManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

@InjectViewState
class MainPresenter(
    private val timerRepository: TimerRepository,
    private val resourceManager: ResourceManager
) : MvpPresenter<MainView>() {

    companion object {

        private const val START_TIME_SEC = 20
        private const val ADDITIONAL_TIME_SEC = 5
    }

    private var counterDisposable: Disposable? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.startTimerService()
        observeTime()
    }

    override fun onDestroy() {
        counterDisposable?.dispose()
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

    private fun observeTime() {
        counterDisposable = timerRepository.observeTime()
            .subscribeOn(Schedulers.computation())
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::handleTime)
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