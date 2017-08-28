package com.music.app.objects;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Queue
{
    private Data data;
    private ArrayList<Long> queue;
    private int queueStack, queueCount;

    public Queue(Data data)
    {
        this.data = data;
    }

    public void initialize(long[] ids)
    {
        queue = new ArrayList<>();
        for(long id : ids)
            queue.add(id);
    }

    public ArrayList<Long> getQueue()
    {
        return queue;
    }

    void newSong(long id)
    {
        resetQueue();
        data.updateCurrentQueueIndex(queue.indexOf(id));
    }

    private void resetQueue()
    {
        ArrayList<Song> songs = data.songs();
        long[] ids = new long[songs.size()];
        for(int i = 0; i < ids.length; i++)
            ids[i] = songs.get(i).getId();
        initialize(ids);

        queueStack = 0;
        queueCount = 0;
    }

    long update(boolean isNext)
    {
        int index = data.currentQueueIndex();
        if(isNext)
            index = (index < queue.size() - 1)? index + 1 : 0;
        else
            index = (index > 0)? index - 1 : queue.size() - 1;

        data.updateCurrentQueueIndex(index);
        updateQueueStack(isNext);
        return queue.get(index);
    }

    public void queue(long id)
    {
        queueStack++;
        int queueIndex = data.currentQueueIndex() + queueStack;
        queue.add(queueIndex, id);
        queueCount++;
    }

    public void playNext(long id)
    {
        queueStack++;
        int queueIndex = data.currentQueueIndex() + 1;
        queue.add(queueIndex, id);
        queueCount++;
    }

    private void updateQueueStack(boolean isNext)
    {
        if(isNext)
        {
            if(queueStack > 0)
                queueStack--;
        }
        else queueStack++;

        if(queueStack > queueCount || queueStack == 0)
        {
            queueStack = 0;
            queueCount = 0;
        }
    }

    private final String queueStackKey = "queueStack";
    private final String queueCountKey = "queueCount";
    private final String queueSizeKey = "queueSize";

    public void save(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("Queue",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        for(int i = 0; i < queue.size(); i++)
            editor.putLong(String.valueOf(i), queue.get(i));

        editor.putInt(queueStackKey, queueStack);
        editor.putInt(queueCountKey, queueCount);
        editor.putInt(queueSizeKey, queue.size());
        editor.apply();

        data.updateQueueIsSaved(true);
    }

    public void load(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("Queue",
                Context.MODE_PRIVATE);

        queue = new ArrayList<>();
        for(int i = 0; i < preferences.getInt(queueSizeKey, 0) ; i++)
            queue.add(preferences.getLong(String.valueOf(i), 0));

        queueStack = preferences.getInt(queueStackKey, 0);
        queueCount = preferences.getInt(queueCountKey, 0);
    }

    public void shuffle(Context context)
    {
        queueStack = 0;
        queueCount = 0;

        if(data.isShuffled())
        {
            //TODO: Fix current index

            if(data.currentSongIsNotNull())
            {
                long current = queue.get(data.currentQueueIndex());
                queue.remove(current);
                Collections.shuffle(queue);
                queue.add(0, current);
                data.updateCurrentQueueIndex(0);
            }
        }
        else
        {
            ArrayList<Song> songs = data.songs();
            ArrayList<Song> tempSongs = new ArrayList<>();

            for(int i = 0; i < queue.size(); i++)
            {
                for(int j = 0; j < songs.size(); j++)
                {
                    Song song = songs.get(j);
                    if (queue.get(i).equals(song.getId()))
                        tempSongs.add(song);
                }
            }

            Collections.sort(tempSongs, new Comparator<Song>()
            {
                @Override
                public int compare(Song lhs, Song rhs)
                {
                    return lhs.getTitle().compareTo(rhs.getTitle());
                }
            });

            queue.clear();
            for(Song song : tempSongs)
                queue.add(song.getId());

            for(int i = 0; i < queue.size(); i++)
            {
                int duplicates = Collections.frequency(queue, queue.get(i));
                if(duplicates > 1)
                    for(int j = 0; j < duplicates - 1; j++)
                        queue.remove(i);
            }

            if(data.currentSongIsNotNull())
                data.updateCurrentQueueIndex(queue.indexOf
                        (data.currentSong(context).getId()));
        }
    }
}
