package jp.sora0077.janken

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
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
    enum class Result(val rawValue: Int) {
        DRAW(0) {
            override fun resultImageResource(): Int = R.string.result_draw
        },
        WIN(1) {
            override fun resultImageResource(): Int = R.string.result_win
        },
        LOSE(2) {
            override fun resultImageResource(): Int = R.string.result_lose
        };

        abstract fun resultImageResource(): Int
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

        val comHand = getHand()
        comHandImageView.setImageResource(comHand.comHandImageResource())

        val result = Result.values()[(comHand.rawValue - hand.rawValue + 3) % 3]
        resultLabel.setText(result.resultImageResource())

        saveData(hand, comHand, result)
    }

    private fun getHand(): Hand {
        var comHand = Hand.values()[Random().nextInt(3)]
        val pref = PreferenceManager.getDefaultSharedPreferences(this)

        val gameCount = pref.getInt("GAME_COUNT", 0)
        val winningStreakCount = pref.getInt("WINNING_STREAM_COUNT", 0)
        val lastMyHand = pref.getInt("LAST_MY_HAND", 0)
        val lastComHand = pref.getInt("LAST_COM_HAND", 0)
        val beforeLastComHand = pref.getInt("BEFORE_LAST_COM_HAND", 0)
        val gameResult = pref.getInt("GAME_RESULT", -1)

        if (gameCount == 1) {
            if (gameResult == 2) {
                // 前回の勝負が一回目でコンピュータが勝った場合、コンピュータは次に出す手を変える
                while (lastComHand == comHand.rawValue) {
                    comHand = Hand.values()[Random().nextInt(3)]
                }
            } else if (gameResult == 1) {
                comHand = Hand.values()[(lastMyHand - 1 + 3) % 3]
            }
        } else if (winningStreakCount > 0) {
            if (beforeLastComHand == lastComHand) {
                // 同じ手で連勝した場合は手を変える
                while (lastComHand == comHand.rawValue) {
                    comHand = Hand.values()[Random().nextInt(3)]
                }
            }
        }
        return comHand
    }

    private fun saveData(hand: Hand, comHand: Hand, result: Result) {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        pref.edit().apply {
            val gameCount = pref.getInt("GAME_COUNT", 0)
            val winningStreakCount = pref.getInt("WINNING_STREAM_COUNT", 0)
            val lastComHand = pref.getInt("LAST_COM_HAND", 0)
            val lastGameResult = pref.getInt("GAME_RESULT", -1)

            putInt("GAME_COUNT", gameCount + 1)
            if (lastGameResult == 2 && result == Result.LOSE) {
                // コンピュータの連勝
                putInt("WINNING_STREAM_COUNT", winningStreakCount + 1)
            } else {
                putInt("WINNING_STREAM_COUNT", 0)
            }
            putInt("LAST_MY_HAND", hand.rawValue)
            putInt("LAST_COM_HAND", comHand.rawValue)
            putInt("BEFORE_LAST_COM_HAND", lastComHand)
            putInt("GAME_RESULT", result.rawValue)

            commit()
        }
    }
}
