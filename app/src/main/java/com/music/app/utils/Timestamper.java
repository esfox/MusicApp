package com.music.app.utils;

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
}
