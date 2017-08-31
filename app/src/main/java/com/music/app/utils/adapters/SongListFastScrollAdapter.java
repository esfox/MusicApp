package com.music.app.utils.adapters;

import android.content.Context;
import android.widget.ListView;
import android.widget.SectionIndexer;

import com.music.app.fragments.FragmentManager;
import com.music.app.objects.Song;
import com.music.app.objects.Sorter;

import java.util.ArrayList;
import java.util.Collections;

public class SongListFastScrollAdapter extends SongListAdapter implements SectionIndexer
{
    private ArrayList<SongListAdapter.Item> items;

    private ArrayList<String> sections;
    private ArrayList<Integer> sectionIndices;
    private ArrayList<Integer> indexSections;

    private Sorter.SortBy sort;

    public SongListFastScrollAdapter(Context pContext, ArrayList<Song> pSongs, ListView pListView, FragmentManager fragmentManager, Sorter.SortBy pSort)
    {
        super(pContext, pListView, pSongs, fragmentManager, pSort);
        items = getItems();
        sort = pSort;
        manageSections();
    }

    private void manageSections()
    {
        //TODO: Fix (Use items list)

        sections = new ArrayList<>();
        sectionIndices = new ArrayList<>();
        indexSections = new ArrayList<>();

        for(int i = 0, sectionNumber = -1; i < items.size(); i++)
        {
            if(sort != Sorter.SortBy.title)
            {
                if(getItemViewType(i) != SongListAdapter.type_item)
                {
                    String section = items.get(i).getSection();

                    if(!sections.contains(section))
                    {
                        sections.add(section.substring(0, 1).toUpperCase());
                        sectionIndices.add(i);
                        sectionNumber++;
                    }
                }
            }
            else
            {
                String detail;
                if(sort == Sorter.SortBy.title)
                    detail = items.get(i).getSong().getTitle();
                else if(sort == Sorter.SortBy.artist)
                    detail = items.get(i).getSong().getArtist();
                else if(sort == Sorter.SortBy.album)
                    detail = items.get(i).getSong().getAlbum();
                else
                    detail = items.get(i).getSong().getTitle();

                String firstLetter = detail.substring(0, 1).toUpperCase();

                if(!sections.contains(firstLetter))
                {
                    sections.add(firstLetter);
                    sectionIndices.add(i);
                    sectionNumber++;
                }
            }

            indexSections.add(sectionNumber);
        }

        Collections.sort(sections);
    }

    @Override
    public Object[] getSections()
    {
        return sections.toArray();
    }

    @Override
    public int getPositionForSection(int sectionIndex)
    {
        return sectionIndices.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position)
    {
        return indexSections.get(position);
    }
}
