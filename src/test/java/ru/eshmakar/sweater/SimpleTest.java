package ru.eshmakar.sweater;

import org.junit.Assert;
import org.junit.Test;

public class SimpleTest {
    @Test
    public void test(){
        int x = 22;
        int y = 2;
        Assert.assertEquals(44, x*y);
        Assert.assertEquals(24, x+y);
        Assert.assertEquals(11, x/y);
    }

    @Test
    public void test2(){
        Assert.assertEquals("hi","hi");
    }

    //проверка на выбрасывания исключении
    @Test(expected = ArithmeticException.class)
    public void error(){
        int i = 0;
        int b = 1 / i;


    }
}
