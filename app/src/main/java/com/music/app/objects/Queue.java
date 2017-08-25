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

    private void log()
    {
        Log.d("Queue", "Queue Stack: " +
                String.valueOf(queueStack) + "\n" +
                "Queue Count: " +
                String.valueOf(queueCount));
    }

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

    public void newSong(long id)
    {
        data.updateCurrentQueueIndex(queue.indexOf(id));
    }

    public long update(boolean isNext)
    {
        int index = data.currentQueueIndex();
        if(isNext)
            index = (index < queue.size() - 1)? index + 1 : 0;
        else
            index = (index > 0)? index - 1 : queue.size() - 1;

        data.updateCurrentQueueIndex(index);
        updateQueueStack(isNext);
        return queue.get(data.currentQueueIndex());
    }

    public void queue(long id)
    {
        queueStack++;
        queue.add(data.currentQueueIndex() + queueStack, id);
        queueCount++;

        log();
    }

    public void playNext(long id)
    {
        queueStack++;
        queue.add(data.currentQueueIndex() + 1, id);
        queueCount++;

        log();
    }

    private void updateQueueStack(boolean isNext)
    {
        if(isNext)
        {
            if(queueStack > 0)
                queueStack--;
        }
        else queueStack++;

        if(queueStack >= queueCount || queueStack == 0)
        {
            queueStack = 0;
            queueCount = 0;
        }

        log();
    }

    private final String queueSizeKey = "queueSize";

    public void save(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("Queue",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        for(int i = 0; i < queue.size(); i++)
            editor.putLong(String.valueOf(i), queue.get(i));

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
    }

    public void shuffle(Context context)
    {
        queueStack = 0;

        if(data.isShuffled())
        {
            Collections.shuffle(queue);

            if(data.currentSongIsNotNull())
            {
                long current = queue.get(data.currentQueueIndex());
                queue.remove(data.currentQueueIndex());
                queue.add(0, current);
                data.updateCurrentQueueIndex(0);
            }
        }
        else
        {
            Collections.sort(queue, new Comparator<Long>()
            {
                @Override
                public int compare(Long lhs, Long rhs)
                {
                    return lhs.compareTo(rhs);
                }

                @Override
                public boolean equals(Object object)
                {
                    return false;
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
            data.updateCurrentQueueIndex(queue.indexOf
                    (data.currentSong(context).getId()));
    }

    public ArrayList<Long> getQueue()
    {
        return queue;
    }
}
