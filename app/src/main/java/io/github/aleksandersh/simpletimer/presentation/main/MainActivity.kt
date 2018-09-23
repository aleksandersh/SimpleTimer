package io.github.aleksandersh.simpletimer.presentation.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AlertDialog
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import io.github.aleksandersh.simpletimer.R
import io.github.aleksandersh.simpletimer.di.DI
import io.github.aleksandersh.simpletimer.presentation.service.TimerService
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : MvpAppCompatActivity(), MainView, ServiceConnection {

    companion object {

        private const val KEY_CLICK_COUNT = "key_click_count"
    }

    @InjectPresenter
    internal lateinit var presenter: MainPresenter

    @Inject
    internal lateinit var presenterFactory: MainPresenterFactory

    @ProvidePresenter
    internal fun providePresenter() = presenterFactory.create()

    private var boundService: TimerService.TimerBinder? = null
    private var clickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        DI.applicationComponent.inject(this)
        super.onCreate(savedInstanceState)

        savedInstanceState
            ?.getInt(KEY_CLICK_COUNT)
            ?.let { clickCount = it }

        setContentView(R.layout.activity_main)

        activity_main_button_add_time.setOnClickListener { onClickAddTime() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_CLICK_COUNT, clickCount)
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder) {
        service as TimerService.TimerBinder
        service.setTimerListener(presenter::onNextTimerTick)
        boundService = service
        presenter.onServiceConnected()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        boundService = null
    }

    override fun setClickCountText(text: String) {
        activity_main_text_view_click_count.text = text
    }

    override fun setTimerText(text: String) {
        activity_main_text_view_timer.text = text
    }

    override fun startTimerService() {
        startService(Intent(this, TimerService::class.java))
    }

    override fun stopTimerService() {
        boundService?.stop()
    }

    override fun bindTimerService() {
        bindService(Intent(this, TimerService::class.java), this, Context.BIND_AUTO_CREATE)
    }

    override fun unbindTimerService() {
        boundService?.setTimerListener(null)
        unbindService(this)
    }

    override fun startTimer(time: Int) {
        boundService?.start(time)
    }

    override fun restartTimer(time: Int) {
        boundService?.restart(time)
    }

    override fun addTime(time: Int) {
        boundService?.addTime(time)
    }

    override fun finishApplication() {
        boundService?.stop()
        finish()
    }

    override fun showFinishDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.main_finish_dialog_title)
            .setMessage(R.string.main_finish_dialog_message)
            .setNegativeButton(R.string.main_finish_dialog_retry) { _, _ ->
                presenter.onClickRetry()
            }
            .setPositiveButton(R.string.main_finish_dialog_finish) { _, _ ->
                presenter.onClickFinish()
            }
            .setCancelable(false)
            .show()
    }

    private fun onClickAddTime() {
        presenter.onClickAddTime(++clickCount)
    }
}
