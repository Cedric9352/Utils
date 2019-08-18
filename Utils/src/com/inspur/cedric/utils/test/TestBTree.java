package com.inspur.cedric.utils.test;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.inspur.cedric.utils.BTree;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestBTree
{
    private static BTree<Integer, String> bTree;
    private static Map<Integer, String> dataBase;

    static {
        dataBase = new TreeMap<>();
        dataBase.put(39, "a");
        dataBase.put(22, "b");
        dataBase.put(97, "b");
        dataBase.put(41, "d");
        dataBase.put(53, "e");
        dataBase.put(13, "f");
        dataBase.put(21, "g");
        dataBase.put(40, "h");
    }

    @BeforeClass
    public static void setup() {
        System.out.println("begin");
        bTree = new BTree<>(5);
        for(Entry<Integer, String> entry : dataBase.entrySet()) {
            bTree.insert(entry.getKey(), entry.getValue());
        }
    }

    @Test
    public void testInsert() {
        bTree.insert(30, "i");
        bTree.insert(27, "j");
        bTree.insert(33, "k");
        bTree.insert(36, "l");
        bTree.insert(35, "m");
        bTree.insert(34, "n");
        bTree.insert(24, "o");
        bTree.insert(29, "p");
        bTree.insert(26, "q");
        bTree.insert(17, "r");
        bTree.insert(23, "s");
        bTree.insert(28, "t");
        bTree.insert(29, "u");
        bTree.insert(31, "v");
        bTree.insert(32, "w");
        Assert.assertTrue(bTree.check());
    }

    @Test
    public void testRemove() {
        bTree.remove(21);
        bTree.remove(27);
        bTree.remove(32);
        bTree.remove(40);
        Assert.assertTrue(bTree.check());
    }
    
    @AfterClass
    public static void finish() {
        System.out.println("done");
    }
}
