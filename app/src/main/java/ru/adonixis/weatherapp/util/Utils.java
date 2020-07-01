package ru.adonixis.weatherapp.util;


import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Utils {

    public static void showSnackbar(View view,
                                    Snackbar.Callback callback,
                                    @ColorInt int backgroundColor,
                                    @ColorInt int textColor,
                                    String text,
                                    @ColorInt int actionTextColor,
                                    String actionText,
                                    View.OnClickListener onClickListener) {
        if (onClickListener == null) {
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {}
            };
        }
        Snackbar snackbar = Snackbar
                .make(view, text, Snackbar.LENGTH_LONG)
                .addCallback(callback)
                .setActionTextColor(actionTextColor)
                .setAction(actionText, onClickListener);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(backgroundColor);
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(textColor);
        snackbar.show();
    }

    public static String getDate(long time) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(time * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMMM", new Locale("ru","RU"));
        String date = sdf.format(calendar.getTime());
        return date;
    }

    public static String capitalize(String str, int begin, int end) {
        String substring = str.substring(begin, end);
        String result = substring.toUpperCase() + str.substring(end);
        return result;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null) && (activeNetworkInfo.isConnected());
    }

    public static int convertDpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int convertPxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
