package com.venturebridge.service;

import com.venturebridge.entity.Bookmark;
import com.venturebridge.entity.Startup;
import com.venturebridge.entity.User;

import java.util.List;

public interface BookmarkService {

    Bookmark bookmarkStartup(User investor, Startup startup);

    void removeBookmark(User investor, Startup startup);

    List<Bookmark> findBookmarks(User investor);

    long countBookmarks(User investor);
}