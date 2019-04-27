package com.cjh.cloudnews

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

/**
 * 开屏页
 */
class Splash : AppCompatActivity() {

    var isJump = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        doAsync {
            for (i in 2 downTo 0) {
                Thread.sleep(1000)
                uiThread {
                    val s = "跳过 ${i}s"
                    jumpOver.text = s
                }
            }
            if (!isJump) {
                jump()
            }
        }

        initView()
    }

    private fun initView() {

        jumpOver.setOnClickListener {
            if (!isJump) {
                jump()
            }
        }
    }

    private fun jump() {
        isJump = true
        startActivity<MainActivity>()
        finish()
    }


}
