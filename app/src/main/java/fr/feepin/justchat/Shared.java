package fr.feepin.justchat;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

//A class that contains static object for distribution purposes over fragments
public class Shared {
    public static String wasOn;
    public static AppCompatActivity appCompatActivity;

    //Databas-Api
    public static Database database;
    public static RequestQueue requestQueue;
    public static JSONObject datas;
    public static Socket socket;

    //Session info
    public static String hostname;
    public static String userName;
    public static String theme;
    public static int userCount;

}
