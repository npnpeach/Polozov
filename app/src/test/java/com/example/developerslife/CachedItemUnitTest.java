package com.example.developerslife;

import org.junit.Test;
import com.example.developerslife.CachedItem;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CachedItemUnitTest {
    @Test
    public void getterImgUrl_isCorrect() {
        String imgUrl = "https://developerslife.ru/random?json=true";
        String description = "Test description";
        CachedItem cachedItem = new CachedItem(imgUrl,description);
        assertEquals(cachedItem.getImgUrl(), imgUrl);
    }
    @Test
    public void getterDescription_isCorrect() {
        String imgUrl = "https://developerslife.ru/random?json=true";
        String description = "Test description";
        CachedItem cachedItem = new CachedItem(imgUrl,description);
        assertEquals(cachedItem.getDescription(), description);
    }
}