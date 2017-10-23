package com.music.app.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Queue
{
    private Data data;
    private ArrayList<Long> queue;
    private int queueStack, queueCount;

    private long[] defaultIDs;

    public Queue(Data data)
    {
        this.data = data;
    }

    public void setDefaultIDs()
    {
        ArrayList<Song> songs = data.songs();
        defaultIDs = new long[songs.size()];
        for(int i = 0; i < defaultIDs.length; i++)
            defaultIDs[i] = songs.get(i).getID();
    }

    public void initialize(long[] ids)
    {
        queue = new ArrayList<>();
        for(long id : ids)
            queue.add(id);
    }

    public ArrayList<Long> getQueueList()
    {
        return queue;
    }

    void newSong(long songID)
    {
        resetQueue(defaultIDs);
        shuffle(data.currentSong(), data.isShuffled());
        data.updateCurrentQueueIndex(queue.indexOf(songID));
    }

    void newSongFromPlaylist(long songID, Playlist playlist)
    {
        resetQueue(playlist.getSongs());
        shuffle(data.currentSong(), data.isShuffled());
        data.updateCurrentQueueIndex(queue.indexOf(songID));
    }

    public void newSongFromQueue(int previousIndex, int newIndex)
    {
        if(newIndex <= previousIndex + queueStack && newIndex >= previousIndex)
            queueStack = (previousIndex + queueStack) - newIndex;
        else
            queueStack = 0;

        Log.d("New Queue Stack", String.valueOf(queueStack));
    }

    private void resetQueue(long[] ids)
    {
        initialize(ids);

        queueStack = 0;
        queueCount = 0;
    }

    public void reorderQueue(int from, int to)
    {
        int current = data.currentQueueIndex();

        Long item = queue.get(from);
        queue.remove(from);
        queue.add(to, item);

        if(from == current)
        {
            if(to <= current + queueStack && to >= current)
                queueStack = (current + queueStack) - to;
            else
                queueStack = 0;
        }
        else if(from < current)
        {
            if(to <= current + queueStack && to >= current)
                queueStack++;
        }
        else if(from > current + queueStack)
        {
            if(to <= current + queueStack && to > current)
                queueStack++;
        }
        else if(from > current && from <= current + queueStack)
        {
            if(to > current + queueStack || to <= current)
                queueStack--;
        }
        else
            queueStack = 0;

        if(from != to)
        {
            int index = current;
            if(from != current)
            {
                if (to > current && from < current)
                    index--;
                else if (to < current && from > current)
                    index++;
                else if (to == current)
                    if (to > from)
                        index--;
                    else if (to < from)
                        index++;
            }
            else
                index = to;

            data.updateCurrentQueueIndex(index);
        }
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

    public void queue(long id, boolean queueNext, Context context)
    {
        queueStack++;
        int queueIndex = queueNext?
                data.currentQueueIndex() + 1 :
                data.currentQueueIndex() + queueStack;
        queue.add(queueIndex, id);
        queueCount++;

        save(context);
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

    void shuffle(Song currentSong, boolean isShuffled)
    {
        data.updateIsShuffled(isShuffled);

        queueStack = 0;
        queueCount = 0;

        if(isShuffled)
        {
            if(data.currentSongIsNotNull())
            {
                long current = currentSong.getID();
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
                    if (queue.get(i).equals(song.getID()))
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
                queue.add(song.getID());

            for(int i = 0; i < queue.size(); i++)
            {
                int duplicates = Collections.frequency(queue, queue.get(i));
                if(duplicates > 1)
                    for(int j = 0; j < duplicates - 1; j++)
                        queue.remove(i);
            }

            if(data.currentSongIsNotNull())
                data.updateCurrentQueueIndex(queue.indexOf
                        (currentSong.getID()));
        }
    }

    private final String queuePrefsName = "Queue";
    private final String queueStackKey = "queueStack";
    private final String queueCountKey = "queueCount";
    private final String queueSizeKey = "queueSize";

    void save(Context context)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(queuePrefsName,
                Context.MODE_PRIVATE).edit();

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
        SharedPreferences preferences = context.getSharedPreferences(queuePrefsName,
                Context.MODE_PRIVATE);

        queue = new ArrayList<>();
        for(int i = 0; i < preferences.getInt(queueSizeKey, 0) ; i++)
            queue.add(preferences.getLong(String.valueOf(i), 0));

        queueStack = preferences.getInt(queueStackKey, 0);
        queueCount = preferences.getInt(queueCountKey, 0);
    }
}
