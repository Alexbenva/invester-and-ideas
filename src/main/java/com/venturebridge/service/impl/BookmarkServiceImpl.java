package com.venturebridge.service.impl;

import com.venturebridge.entity.Bookmark;
import com.venturebridge.entity.Startup;
import com.venturebridge.entity.User;
import com.venturebridge.repository.BookmarkRepository;
import com.venturebridge.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    @Override
    public Bookmark bookmarkStartup(User investor, Startup startup) {
        return bookmarkRepository.findByInvestorAndStartup(investor, startup)
                .orElseGet(() -> bookmarkRepository.save(new Bookmark(investor, startup)));
    }

    @Override
    public void removeBookmark(User investor, Startup startup) {
        bookmarkRepository.findByInvestorAndStartup(investor, startup)
                .ifPresent(bookmarkRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bookmark> findBookmarks(User investor) {
        return bookmarkRepository.findByInvestorWithStartup(investor);
    }

    @Override
    @Transactional(readOnly = true)
    public long countBookmarks(User investor) {
        return bookmarkRepository.countByInvestor(investor);
    }
}