package github.poicraft.poimanager.tiles

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import github.poicraft.poimanager.R
import github.poicraft.poimanager.checkAuth
import github.poicraft.poimanager.cmd.WhitelistManagerActivity
import github.poicraft.poimanager.sendCommand

@RequiresApi(Build.VERSION_CODES.N)
class WhitelistManagerTile: TileService(){

    private lateinit var serverAddress: String
    private lateinit var serverToken: String

    override fun onStartListening() {
        val sp: SharedPreferences = getSharedPreferences("server", Context.MODE_MULTI_PROCESS)
        serverAddress = sp.getString("address", "")!!
        serverToken = sp.getString("token", "")!!
        qsTile.state = Tile.STATE_INACTIVE
        if (serverAddress == "" || serverToken == "") {
            qsTile.state = Tile.STATE_INACTIVE
        }else{
            checkAuth(serverAddress, serverToken){result ->
                if (result)
                    qsTile.state = Tile.STATE_ACTIVE
            }
        }
        super.onStartListening()
    }

    override fun onClick() {

        if (qsTile.state == Tile.STATE_ACTIVE)
            startActivity(Intent(this, WhitelistManagerActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

        super.onClick()
    }
}