/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbs.mitbbs.exceptions;

/**
 *
 * @author Tao Zhao
 */
public class NullHttpDocumentException extends Exception {
    /**
     *
     * @param message
     */
    public NullHttpDocumentException(String message){
        super(message);
    }
}
