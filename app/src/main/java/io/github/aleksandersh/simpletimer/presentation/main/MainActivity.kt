package io.github.aleksandersh.simpletimer.presentation.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import io.github.aleksandersh.simpletimer.R
import io.github.aleksandersh.simpletimer.presentation.service.TimerService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ServiceConnection {

    companion object {

        private const val KEY_CLICK_COUNT = "key_click_count"
        private const val KEY_TIMER_TICK = "key_timer_tick"

        private const val START_TIME_SEC = 20
        private const val ADDITIONAL_TIME_SEC = 5
    }

    private var boundService: TimerService.TimerBinder? = null
    private var clickCount = 0
    private var timerTick = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            val clickCount = savedInstanceState.getInt(KEY_CLICK_COUNT, 0)
            setClickCount(clickCount)
            val timerTick = savedInstanceState.getInt(KEY_TIMER_TICK, 0)
            setTimerTick(timerTick)
        } else {
            startTimerService()
        }

        activity_main_button_add_time.setOnClickListener { onClickAddTime() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_CLICK_COUNT, clickCount)
        outState.putInt(KEY_TIMER_TICK, timerTick)
    }

    override fun onResume() {
        super.onResume()
        bindTimerService()
    }

    override fun onPause() {
        super.onPause()
        unbindTimerService()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder) {
        service as TimerService.TimerBinder
        service.setTimerListener(::onNextTimerTick)
        boundService = service
        startTimer(START_TIME_SEC)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        boundService = null
    }

    private fun setClickCount(count: Int) {
        clickCount = count
        val text = if (count > 0) {
            resources.getQuantityString(R.plurals.main_click_count, count, count)
        } else {
            null
        }
        activity_main_text_view_click_count.text = text
    }

    private fun setTimerTick(tick: Int) {
        timerTick = tick
        activity_main_text_view_timer.text = tick.toString()
    }

    private fun startTimerService() {
        startService(Intent(this, TimerService::class.java))
    }

    private fun stopTimerService() {
        boundService?.stop()
    }

    private fun bindTimerService() {
        bindService(Intent(this, TimerService::class.java), this, Context.BIND_AUTO_CREATE)
    }

    private fun unbindTimerService() {
        boundService?.setTimerListener(null)
        unbindService(this)
    }

    private fun startTimer(time: Int) {
        boundService?.start(time)
    }

    private fun restartTimer(time: Int) {
        boundService?.restart(time)
    }

    private fun addTime(time: Int) {
        boundService?.addTime(time)
    }

    private fun finishApplication() {
        boundService?.stop()
        finish()
    }

    private fun showFinishDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.main_finish_dialog_title)
            .setMessage(R.string.main_finish_dialog_message)
            .setNegativeButton(R.string.main_finish_dialog_retry) { _, _ ->
                restartTimer(START_TIME_SEC)
            }
            .setPositiveButton(R.string.main_finish_dialog_finish) { _, _ ->
                stopTimerService()
                finishApplication()
            }
            .setCancelable(false)
            .show()
    }

    private fun onClickAddTime() {
        setClickCount(++clickCount)
        addTime(ADDITIONAL_TIME_SEC)
    }

    private fun onNextTimerTick(tick: Int) {
        setTimerTick(tick)
        if (tick <= 0) {
            showFinishDialog()
        }
    }
}
