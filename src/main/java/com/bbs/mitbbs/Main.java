/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbs.mitbbs;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author arronzhao
 */
public class Main {
    public static void main(String[] args){
        WebCrower crower = new WebCrower();
        while(true){
            crower.getPostByAuthor("gttzt");
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
