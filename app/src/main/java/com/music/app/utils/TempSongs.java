package com.music.app.utils;

import java.util.ArrayList;
import java.util.Collections;

public class TempSongs
{
    private ArrayList<TempSong> temps;

    public TempSongs()
    {
        temps = new ArrayList<>();
        createInfo();
    }

    public class TempSong
    {
        private String title,
                artist,
                album;

        TempSong(String pTitle, String pArtist, String pAlbum)
        {
            title = pTitle;
            artist = pArtist;
            album = pAlbum;
        }

        public String getTitle()
        {
            return title;
        }

        public String getArtist()
        {
            return artist;
        }

        public String getAlbum()
        {
            return album;
        }
    }

    private void createInfo()
    {
        String[] titles  = new String[]
                {
                        "C.H.A.O.S.M.Y.T.H",
                        "No Scared",
                        "Cry Out",
                        "Stuck in the Middle",
                        "Shadow of the Day",
                        "Breaking the Habit",
                        "Numb",
                        "From The Inside",
                        "My Immortal",
                        "Call Me When You're Sober",
                        "Make Me Wanna Die",
                        "BlackHeart",
                        "El Dorado",
                        "WinterSpell",
                        "Age Of Gods",
                        "The Beginning",
                        "Bury Me Alive",
                        "I Will Stay",
                        "Sleep Well, My Angel",
                        "Through Hell",
                        "Carolyn",
                        "We Stich These Wounds",
                        "Go",
                        "All I Need Is You",
                        "Even When It Hurts",

                        "Take Heart",
                        "Endless Praise",
                        "Ever Be",
                        "Faithful",
                        "A Song to Sing",
                        "Runnning",
                        "My God",
                        "Aftermath",
                        "Mr. Feather",
                        "Real Love",
                        "Brick By Boring",
                        "We Dance",
                        "Dance in Freedom",
                        "Animals",
                        "Alone",
                        "Relentless",
                        "Scandal of Grace",
                        "Joy",
                        "Alive",
                        "Where You Are",
                        "Many Waters",
                        "Closer Than You Know",
                        "This Is Our Time",
                        "Get Up",
                        "Still Into You"
                },
                 artists = new String[]
                {
                        "ONE OK ROCK",
                        "ONE OK ROCK",
                        "ONE OK ROCK",
                        "ONE OK ROCK",
                        "Linkin Park",
                        "Linkin Park",
                        "Linkin Park",
                        "Linkin Park",
                        "Linkin Park",
                        "Evanescense",
                        "Evanescense",
                        "The Pretty Reckless",
                        "Two Steps From Hell",
                        "Two Steps From Hell",
                        "Two Steps From Hell",
                        "Two Steps From Hell",
                        "We Are The Fallen",
                        "We Are The Fallen",
                        "We Are The Fallen",
                        "We Are The Fallen",
                        "Black Veiled Brides",
                        "Black Veiled Brides",
                        "Hillsong UNITED",
                        "Hillsong UNITED",
                        "Hillsong UNITED",

                        "Hillsong UNITED",
                        "Planetshakers",
                        "Bethel Music",
                        "Victory Worship",
                        "Hillsong UNITED",
                        "Hillsong",
                        "Victory Worship",
                        "Hillsong UNITED",
                        "Ellegarden",
                        "Hillsong Young & Free",
                        "Paramore",
                        "Bethel Music",
                        "Victory Worship",
                        "Martin Garrix",
                        "Marshmello",
                        "Hillsong UNITED",
                        "Hillsong UNITED",
                        "Planetshakers",
                        "Hillsong Young & Free",
                        "Hillsong Young & Free",
                        "Every Nation Music",
                        "Hillsong UNITED",
                        "Planetshakers",
                        "Planetshakers",
                        "Paramore"
                },
                 albums  = new String[]
                {
                        "Zankyo Reference",
                        "Zankyo Reference",
                        "35xxxv",
                        "35xxxv",
                        "Meteora",
                        "Minutes to Midnight",
                        "Meteora",
                        "Meteora",
                        "Meteora",
                        "Fallen",
                        "The Open Door",
                        "Light Me Up",
                        "SkyWorld",
                        "SkyWorld",
                        "SkyWorld",
                        "Illusions",
                        "Tear the World Down",
                        "Tear the World Down",
                        "Tear the World Down",
                        "Tear the World Down",
                        "We Stich These Wounds",
                        "We Stich These Wounds",
                        "Aftermath",
                        "All I Need Is You",
                        "Even When It Hurts",

                        "Live in Miami",
                        "Endless Praise",
                        "We Will Not Be Shaken",
                        "Radical Love",
                        "Live in Miami",
                        "Cornerstone",
                        "Rise Heart",
                        "Aftermath",
                        "Ellegarden Top Hits",
                        "Youth Revival",
                        "Brand New Eyes",
                        "You Make Me Brave",
                        "Rise Heart",
                        "Spinnin' Records",
                        "Alone - Single",
                        "Zion",
                        "Zion",
                        "This is Our Time",
                        "We Are Young & Free",
                        "Where You Are - Single",
                        "Wings EP",
                        "Empires",
                        "This Is Our Time",
                        "One",
                        "Still Into You - Single"
                };

        for(int i = 0; i < titles.length; i++)
        {
            temps.add(new TempSong(titles[i], artists[i], albums[i]));
        }

    }

    public ArrayList<TempSong> getSongs()
    {
        return temps;
    }
}
