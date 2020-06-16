package fr.feepin.justchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.RequestQueue;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

public class Shared {
    public static Database database;
    public static String theme;
    public static RequestQueue requestQueue;
    public static Socket socket;
    public static String userName;
    public static AppCompatActivity appCompatActivity;
    public static JSONObject datas;
    public static String hostname;
    public static String wasOn;
    public static int userCount;
}
