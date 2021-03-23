/**
 * Author: Adam Nunez, adam.a.nunez@gmail.com
 * Copyright (C) 2014 Adam Nunez
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package Chaos.LFSR;

import Chaos.Logistical;

public class LFSRSimulator extends LFSR {
    
    public static void main(String[] args) throws Exception {

        int length = 100000;
        long startTime = System.nanoTime();    //获取开始时间

        LFSR lfsr = new LFSR ();
        lfsr.setNumberOfBits (length);
        lfsr.setGateType (GateType.XOR);

        long time = System.nanoTime();
        for(int i=0;i<length;i++)
            lfsr.strobeClock (); // Move the LFSR forward one.

        long time1 = System.nanoTime();
        lfsr.strobeClock ();

        long endTime = System.nanoTime();    //获取结束时间
        System.out.println("程序运行时间：" + (endTime - time1+time-startTime) );    //输出程序运行时间


        startTime = System.nanoTime();    //获取开始时间
        double u=3.98;
        double x=0.5;
        Logistical logistical = new Logistical(u,x);
        logistical.GetSqueue(length);
        endTime = System.nanoTime();    //获取结束时间
        System.out.println("程序运行时间：" + (endTime - startTime));    //输出程序运行时间
    }

}