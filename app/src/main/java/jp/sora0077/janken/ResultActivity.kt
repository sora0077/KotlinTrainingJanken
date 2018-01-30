package jp.sora0077.janken

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_result.*
import java.util.*

class ResultActivity: AppCompatActivity() {
    enum class Hand(val rawValue: Int) {
        GU(0) {
            override fun myHandImageResource(): Int = R.drawable.gu
            override fun comHandImageResource(): Int = R.drawable.com_gu
        },
        CHOKI(1) {
            override fun myHandImageResource(): Int = R.drawable.choki
            override fun comHandImageResource(): Int = R.drawable.com_choki
        },
        PA(2) {
            override fun myHandImageResource(): Int = R.drawable.pa
            override fun comHandImageResource(): Int = R.drawable.com_pa
        };

        abstract fun myHandImageResource(): Int
        abstract fun comHandImageResource(): Int
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        backButton.setOnClickListener {
            finish()
        }

        val idx = when (intent.getIntExtra("MY_HAND", 0)) {
            R.id.guButton -> 0
            R.id.chokiButton -> 1
            R.id.paButton -> 2
            else -> throw Error("undefined value")
        }
        val hand = Hand.values()[idx]
        myHandImageView.setImageResource(hand.myHandImageResource())

        val comHand = Hand.values()[Random().nextInt(3)]
        comHandImageView.setImageResource(comHand.comHandImageResource())

        resultLabel.setText(
            when ((comHand.rawValue - hand.rawValue + 3) % 3) {
                0 -> R.string.result_draw
                1 -> R.string.result_win
                2 -> R.string.result_lose
                else -> throw Error("undefined value")
            }
        )
    }
}
