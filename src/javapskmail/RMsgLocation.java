/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javapskmail;

/**
 *
 * @author jdouyere
 */
public class RMsgLocation {
    private double latitude;
    private double longitude;
    private double speed;
    
    //To be completed 
    public RMsgLocation(String locString) {
        
    }
    
    public void setSpeed(double mySpeed) {
        this.speed = mySpeed;
    }
    public void setLatitude(double myLatitude) {
        this.latitude = myLatitude;
    }
    public void setLongitude(double myLongitude) {
        this.longitude = myLongitude;
    }
    
    
    public double getLatitude() {
        return this.latitude;
    }
    public double getLongitude() {
        return this.longitude;
    }
    public double getSpeed() {
        return this.speed;
    }
    
    
    
}
