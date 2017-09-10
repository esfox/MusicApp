package com.music.app.utils;

import java.util.Locale;

public class Timestamper
{
    public static String getTimestamp(long milliseconds)
    {
        String duration = "";
        String secondsString;

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if(hours > 0)
            duration = hours + ":";

        // Prepending 0 to seconds if it is ONE digit
        if(seconds < 10)
            secondsString = "0" + seconds;
        else
            secondsString = "" + seconds;

        duration += minutes + ":" + secondsString;

        return duration;
    }

    public static String getIndicatorTimestamp(long milliseconds)
    {
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        String format = "%d:%02d";
        if(hours > 0)
        {
            if(hours >= 10)
                format = "%02d:%02d:%02d";
            else
                format = "%d:%02d:%02d";

            return String.format(Locale.getDefault(), format, hours, minutes, seconds);
        }

        if(minutes > 0)
        {
            if(minutes >= 10)
                format = "%02d:%02d";
            else
                format = "%d:%02d";

            return String.format(Locale.getDefault(), format, minutes, seconds);
        }

        return String.format(Locale.getDefault(), format, minutes, seconds);
    }
}
