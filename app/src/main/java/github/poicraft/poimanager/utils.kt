package github.poicraft.poimanager

import github.gggxbbb.Http
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

fun checkAuth(address: String, token: String, callback: (Boolean) -> Unit){
    Http.get("http://${address}/api/log/all/0?token=${token}",
        {_ , response ->
            val code = JSONObject(response.body!!.string()).getInt("code")
            if (code == 200)
                callback(true)
            else
                callback(false)
        },
        {_, _ ->
            callback(false)
        })
}

fun sendCommand(address: String, token: String, cmd: String, line: Int, callback: (Boolean, JSONArray) -> Unit){

    Http.get("http://${address}/api/cmd/${cmd}?token=${token}&line=${line}",
        {_ , response ->

            val body = JSONObject(response.body!!.string())
            val code = body.getInt("code")

            if (code == 200) {
                val content = body.getJSONArray("content")
                callback(true, content)
            }
            else {
                callback(false, JSONArray().put(
                    JSONObject()
                        .put("log", "Failed")
                    )
                )
            }
        },
        {_, _ ->
            callback(false, JSONArray().put(
                JSONObject()
                    .put("log", "Failed")
                )
            )
        })
}