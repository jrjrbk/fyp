package com.example.testsem7v1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.testsem7v1.activity.gameData
import com.example.testsem7v1.activity.userID
import com.example.testsem7v1.retrofit.retrofitInstance
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val score: TextView = requireView().findViewById(R.id.modScore)

        val badge1: CardView = requireView().findViewById(R.id.cardLock1)
        val badge2: CardView = requireView().findViewById(R.id.cardLock2)
        val badge3: CardView = requireView().findViewById(R.id.cardLock3)
        val badge4: CardView = requireView().findViewById(R.id.cardLock4)

        val badge1TV: TextView = requireView().findViewById(R.id.badgeAchieve1)
        val badge2TV: TextView = requireView().findViewById(R.id.badgeAchieve2)
        val badge3TV: TextView = requireView().findViewById(R.id.badgeAchieve3)
        val badge4TV: TextView = requireView().findViewById(R.id.badgeAchieve4)

        lifecycleScope.launch{
            getGame()
            score.text = gameData.score.toString()

            if(gameData.score in 100..249){
                badge1.visibility = View.GONE

                badge1TV.text= getString(R.string.achievement_unlocked)
            }
            else if(gameData.score in 250..999){
                badge1.visibility = View.GONE
                badge2.visibility = View.GONE

                badge1TV.text= getString(R.string.achievement_unlocked)
                badge2TV.text= getString(R.string.achievement_unlocked)

            }
            else if(gameData.score in 1000..2499){
                badge1.visibility = View.GONE
                badge2.visibility = View.GONE
                badge3.visibility = View.GONE

                badge1TV.text= getString(R.string.achievement_unlocked)
                badge2TV.text= getString(R.string.achievement_unlocked)
                badge3TV.text= getString(R.string.achievement_unlocked)

            }
            else if(gameData.score>=2500){
                badge1.visibility = View.GONE
                badge2.visibility = View.GONE
                badge3.visibility = View.GONE
                badge4.visibility = View.GONE

                badge1TV.text= getString(R.string.achievement_unlocked)
                badge2TV.text= getString(R.string.achievement_unlocked)
                badge3TV.text= getString(R.string.achievement_unlocked)
                badge4TV.text= getString(R.string.achievement_unlocked)

            }
        }
    }


    private suspend fun getGame(): String{

        var id = userID.accountID
        Log.e("HOMEFRAGMENT-GETGOOD", id.toString())
        val response = try {
            retrofitInstance.api.getGame(id)
        } catch (e: IOException) {
            Log.e("HOMEFRAGMENT-GETGOOD", "IOException, you might not have internet connection")
            return "failed"

        } catch (e: HttpException) {
            Log.e("HOMEFRAGMENT-GETGOOD", "HttpException, unexpected response")
            return "failed"
        }
        if (response.isSuccessful && response.body() != null) {
            gameData = response.body()!!
        }

        return "Success"
    }

}