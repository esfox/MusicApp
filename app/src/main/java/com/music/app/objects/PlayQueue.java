package com.music.app.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PlayQueue
{
    private static Data data;

    private static int currentIndex = -1;
    private static int queueStack = 0;

    public static ArrayList<Song> queue = new ArrayList<>();

    public static void setData(Data data)
    {
        PlayQueue.data = data;
    }

    static void newSongPlayed(Song song)
    {
        if(Data.currentSong != null)
        {
            if(song != Data.currentSong)
            {
                if (currentIndex == -1)
                    queue.remove(queue.indexOf(song));
                queue.add(currentIndex + 1, song);
            }
        }
        else
        {
            queue.remove(queue.indexOf(song));
            queue.add(0, song);
        }
    }

    static void newCurrentSongIndex(Song song)
    {
        currentIndex = queue.indexOf(song);
        data.updateCurrentSongQueueIndex(currentIndex);
    }

    static void updateCurrentSongIndex(boolean update, boolean increment)
    {
        if(update)
            if(increment) currentIndex++; else currentIndex--;

        data.updateCurrentSongQueueIndex(currentIndex);
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
        if(Data.currentSong != null)
        {
            queueStack++;
            queue.add(currentIndex + queueStack, song);
        }
    }

    public static void playNext(Song song)
    {
        if(Data.currentSong != null)
        {
            queueStack++;
            queue.add(currentIndex + 1, song);
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
        return (currentIndex < queue.size() - 1)?
                next() : first();
    }

    static Song getPreviousSong()
    {
        return (currentIndex > 0)?
                previous() : last();
    }

    private static Song first()
    {
        return queue.get(0);
    }

    private static Song previous()
    {
        return queue.get(currentIndex - 1);
    }

    private static Song next()
    {
        return queue.get(currentIndex + 1);
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

            if(Data.currentSong != null)
            {
                queue.remove(Data.currentSong);
                queue.add(0, Data.currentSong);
                currentIndex = 0;
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

        if(Data.currentSong != null)
            newCurrentSongIndex(Data.currentSong);
     }
}
