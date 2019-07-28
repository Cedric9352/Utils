package com.inspur.cedric.utils.test;

import org.junit.Assert;
import org.junit.Test;

import com.inspur.cedric.utils.BST;

public class TestBST
{
    @Test
    public void testMain() {
        BST<Integer> bstree = new BST<>();
        bstree.insert(4);
        bstree.insert(2);
        bstree.insert(8);
        bstree.insert(1);
        bstree.insert(3);
        bstree.insert(5);
        bstree.insert(9);
        bstree.insert(6);
        Assert.assertTrue(bstree.remove(4));
        Assert.assertTrue(bstree.remove(5));
        Assert.assertTrue(bstree.remove(1));
        Assert.assertTrue(bstree.check());
    }
}
