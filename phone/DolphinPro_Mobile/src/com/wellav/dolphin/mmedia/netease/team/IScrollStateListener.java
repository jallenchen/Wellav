package com.wellav.dolphin.mmedia.netease.team;

public interface IScrollStateListener {

    /**
     * move to scrap heap
     */
    public void reclaim();


    /**
     * on idle
     */
    public void onImmutable();
}
