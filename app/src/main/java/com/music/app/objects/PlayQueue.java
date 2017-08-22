package com.music.app.objects;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PlayQueue
{
    private static Data data;

    private static int queueStack = 0;

    //TODO: Save this
    public static ArrayList<Song> queue;

    public static void setData(Data data)
    {
        PlayQueue.data = data;
    }

    static void newSongPlayed(Song song)
    {
        if(data.currentSongIsNotNull())
        {
            if(song != Data.currentSong)
            {
                if (data.currentQueueIndex() == -1)
                    queue.remove(queue.indexOf(song));

                int index = data.currentQueueIndex() + 1;
                queue.add(index, song);
                data.updateCurrentQueueIndex(index);
            }
        }
        else
        {
//            queue.remove(queue.indexOf(song));
//            queue.add(0, song);
            data.updateCurrentQueueIndex(queue.indexOf(song));
        }
    }

    static void updateCurrentSongIndex(boolean update, boolean increment)
    {
        if(update)
            if(increment)
            {
                data.updateCurrentQueueIndex(data.currentQueueIndex() + 1);
            }
            else data.updateCurrentQueueIndex(data.currentQueueIndex() - 1);
    }

//    static void logCurrents()
//    {
//        if(data.currentSong != null)
//        {
//            Log.wtf("Current Index", String.valueOf(currentIndex));
//            Log.wtf("Data Current Song", data.currentSong.getTitle());
//            Log.wtf("Index Current Song", queue.get(currentIndex).getTitle());
//
//            if (queue.get(currentIndex).equals(data.currentSong))
//                Log.wtf("Currents Checking", "OHYES!!!");
//            else
//                Log.wtf("Currents Checking", "pls");
//
//            Log.wtf("Queue Number", String.valueOf(queueStack));
//        }
//    }

    public static void queue(Song song)
    {
        if(data.currentSongIsNotNull())
        {
            queueStack++;
            queue.add(data.currentQueueIndex() + queueStack, song);
        }
    }

    public static void playNext(Song song)
    {
        if(data.currentSongIsNotNull())
        {
            queueStack++;
            queue.add(data.currentQueueIndex() + 1, song);
        }
    }

    static void updateQueueStack(boolean decrementQueueNumber)
    {
        if(decrementQueueNumber)
        {
            //Queue Number does not decrease if there is nothing in queue (queueStack = 0)
            if(queueStack > 0)
            {
                //Queue Number decreases on playing a song in queue
                queueStack--;
            }
        }
        else
        {
            //Queue Number does not increase or resets if song position is less than current song position
            if(queueStack > 0)
            {
                //Queue Number increases if current song is within queue
                queueStack++;
            }
        }
    }

//    private static void reset()
//    {
//        queueStack = 0;
//        currentIndex = -1;
//    }

    static Song getNextSong()
    {
        return (data.currentQueueIndex() < queue.size() - 1)?
                next() : first();
    }

    static Song getPreviousSong()
    {
        return (data.currentQueueIndex() > 0)?
                previous() : last();
    }

    private static Song first()
    {
        return queue.get(0);
    }

    private static Song previous()
    {
        return queue.get(data.currentQueueIndex() - 1);
    }

    private static Song next()
    {
        return queue.get(data.currentQueueIndex() + 1);
    }

    private static Song last()
    {
        return queue.get(queue.size() - 1);
    }

    public static void shuffle()
    {
        queueStack = 0;

        if(data.isShuffled())
        {
            Collections.shuffle(queue);

            if(data.currentSongIsNotNull())
            {
                queue.remove(Data.currentSong);
                queue.add(0, Data.currentSong);
                data.updateCurrentQueueIndex(0);
            }
        }
        else
        {
            Collections.sort(queue, new Comparator<Song>()
            {
                @Override
                public int compare(Song lhs, Song rhs)
                {
                    return lhs.getTitle().compareTo(rhs.getTitle());
                }
            });

            for(int i = 0; i < queue.size(); i++)
            {
                int duplicates = Collections.frequency(queue, queue.get(i));
                if(duplicates > 1)
                    for(int j = 0; j < duplicates - 1; j++)
                        queue.remove(i);
            }
        }

        if(data.currentSongIsNotNull())
            data.updateCurrentQueueIndex(Data.songs.indexOf(Data.currentSong));
     }
}
