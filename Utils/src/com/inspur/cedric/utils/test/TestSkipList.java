package com.inspur.cedric.utils.test;

import org.junit.Assert;
import org.junit.Test;

import com.inspur.cedric.utils.SkipList;

public class TestSkipList {

    @Test
    public void testMain() {
        SkipList<Integer> skipList = new SkipList<>(4);
        skipList.insert(33, 1.8);
        skipList.insert(2, 3.5);
        skipList.insert(55, 2.2);
        skipList.visualize();
        Assert.assertNotNull(skipList.search(33));
        Assert.assertNull(skipList.search(45));
        Assert.assertNull(skipList.search(48));
        skipList.remove(33, 1.8);
        skipList.visualize();
        Assert.assertNull(skipList.search(33));
    }
}
