/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javapskmail;

import java.nio.ByteBuffer;

/**
 *
 * @author jdouyere
 */
public class Bitmap {
    private int width;
    private int height;
    
    public int getWidth() {
        return width;
    }
     
    public int getHeight() {
        return height;
    }
     
    public int getXYZ() {
        return 0;
    }
    
    public static Bitmap decodeFile(String fullPicturePath) {
        
        Bitmap bitmap = new Bitmap();
        return bitmap;
    }
    
    
    public void copyPixelsToBuffer(ByteBuffer byteBuffer) {
        
    }
}
