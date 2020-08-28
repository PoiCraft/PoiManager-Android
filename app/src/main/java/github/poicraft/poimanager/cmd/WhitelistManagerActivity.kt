package github.poicraft.poimanager.cmd

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import github.poicraft.poimanager.R
import github.poicraft.poimanager.sendCommand

class WhitelistManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val sp: SharedPreferences = getSharedPreferences("server", Context.MODE_MULTI_PROCESS)
        val serverAddress = sp.getString("address", "")!!
        val serverToken = sp.getString("token", "")!!

        val builder = AlertDialog.Builder(this)
        val builder2 = AlertDialog.Builder(this)

        builder.setTitle(R.string.tile_whitelist)
        builder2.setTitle(R.string.tile_whitelist)

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton(R.string.tile_whitelist_add){ _, _ ->
            sendCommand(serverAddress, serverToken, "whitelist add ${input.text}", 1){ _, result ->
                runOnUiThread{
                    builder2.setMessage(result.getJSONObject(0).getString("log"))
                    val dialog2 = builder2.create()
                    dialog2.show()
                }
            }
        }
        builder.setNegativeButton(R.string.tile_whitelist_remove){ _, _ ->
            sendCommand(serverAddress, serverToken, "whitelist remove ${input.text}", 1){ _, result ->
                runOnUiThread{
                    builder2.setMessage(result.getJSONObject(0).getString("log"))
                    val dialog2 = builder2.create()
                    dialog2.show()
                }
            }
        }



        builder2.setOnDismissListener {
            finish()
        }

        val dialog = builder.create()
        dialog.show()

        builder2.setPositiveButton(R.string.tile_whitelist_done){ dialog2, _ ->
            dialog.dismiss()
            dialog2.dismiss()
        }

        super.onCreate(savedInstanceState)
    }
}