package com.inspur.cedric.utils.test;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.inspur.cedric.utils.Heap;
import com.inspur.cedric.utils.Heap.Type;

public class TestHeap {
    @Test
    public void testMain() {
        Heap<Integer> hMin1 = new Heap<>(Type.MIN);
        hMin1.insert(10);
        hMin1.insert(2);
        hMin1.insert(5);
        Assert.assertEquals(hMin1.peek(), (Integer)2);
        Assert.assertTrue(hMin1.check());
        Heap<Integer> hMin2 = new Heap<>(new Integer[] {10, 2, 3, 6, 1, 9, 5}, Type.MIN);
        Assert.assertEquals(hMin2.peek(), (Integer)1);
        Assert.assertTrue(hMin2.check());

        Heap<Integer> hMax1 = new Heap<>(Type.MAX);
        hMax1.insert(10);
        hMax1.insert(2);
        hMax1.insert(5);
        Assert.assertEquals(hMax1.peek(), (Integer)10);
        Assert.assertTrue(hMax1.check());
        Heap<Integer> hMax2 = new Heap<>(new Integer[] {2, 3, 6, 1, 9, 5}, Type.MAX);
        Assert.assertEquals(hMax2.peek(), (Integer)9);
        Assert.assertTrue(hMax2.check());
        
        Heap<Integer> hMin3 = new Heap<>(Type.MIN);
        Random random = new Random(49);
        for(int i = 0; i < 16; ++i) {
            hMin3.insert(random.nextInt(100));
        }
        Assert.assertTrue(hMin3.getCapacity() == 32);
        Assert.assertTrue(hMin3.check());
        
        hMin3.delete(hMin3.peek());
        Assert.assertTrue(hMin3.getCapacity() == 16);
        Assert.assertTrue(hMin3.check());
    }
}
