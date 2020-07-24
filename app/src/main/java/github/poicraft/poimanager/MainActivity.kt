package github.poicraft.poimanager

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import github.gggxbbb.Http
import github.poicraft.poimanager.addpter.LogAdapter
import github.poicraft.poimanager.addpter.MLog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var wsClient: WebSocket
    private lateinit var adapter: LogAdapter

    private fun connect(){
        try {
            wsClient.close(101, ' '.toString())
        }catch (e: Exception){ }
        wsClient = Http.ws(
            host = "${findViewById<EditText>(R.id.server_address).text.toString()}/api/ws/cmd",
            listener = object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    webSocket.send("{\"token\":\"${server_token.text}\"}")
                    wsClient = webSocket
                    super.onOpen(webSocket, response)
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    val msg = JSONObject(text)
                    if (msg.getString("type")=="auth" && msg.getInt("code")==200){
                        val edit = getSharedPreferences("server", Context.MODE_PRIVATE).edit()
                        edit.putString("address", server_address.text.toString())
                        edit.putString("token", server_token.text.toString())
                        edit.apply()
                        runOnUiThread {
                            fab.hide()
                            cmd_send.visibility = View.VISIBLE
                        }
                    }
                    runOnUiThread {
                        adapter.addLog(
                            MLog(
                                log_msg = msg.getString("msg"),
                                log_type = msg.getString("type")
                            )
                        )
                    }

                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    runOnUiThread {
                        fab.show()
                        cmd_send.visibility = View.GONE
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                    runOnUiThread {
                        adapter.addLog(
                            MLog(
                                log_type = "ws",
                                log_msg = t.message.toString()
                            )
                        )
                    }
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val recManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        log_out.layoutManager = recManager

        adapter = LogAdapter()
        log_out.adapter = adapter

        val sp = getSharedPreferences("server", Context.MODE_PRIVATE)
        server_address.setText(sp.getString("address", ""))
        server_token.setText(sp.getString("token", ""))

        if (server_address.text.toString() != "" && server_token.text.toString() != ""){
            connect()
        }

        fab.setOnClickListener {
            Log.d("main", "clicked")
            connect()
        }

        cmd_send.setOnClickListener {
            wsClient.send(cmd_in.text.toString())
            cmd_in.text.clear()
        }
        }
    }
