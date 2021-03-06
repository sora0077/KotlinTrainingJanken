package jp.sora0077.janken

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        pref.edit().apply {
            clear()
            commit()
        }

        arrayOf(guButton, chokiButton, paButton).forEach {
            it.setOnClickListener {
                val intent = Intent(this@MainActivity, ResultActivity::class.java)
                intent.putExtra("MY_HAND", it.id)
                startActivity(intent)
            }
        }
    }
}
