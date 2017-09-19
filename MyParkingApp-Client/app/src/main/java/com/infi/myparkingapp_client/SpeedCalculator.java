package com.infi.myparkingapp_client;

/**
 * Created by Asus on 17-09-2016.
 */
public class SpeedCalculator {
    private int straight,turn,current;
    private int speed;
    private String Time_Remaining;
    private static final String GO_STRAIGHT="GO Straight";
    private static final String Go_LEFT="Turn Left";
    private static final String GO_RIGHT="Turn Right";
    private boolean IN;
    //send speed in meters/sec
    public SpeedCalculator(int straight,int turn,int speed,int current,boolean IN){
            this.straight=straight;
        this.turn=turn;
        this.speed=speed;
        this.current=current;
    }
    public long GiveTime(int dist){
        return dist/speed;
    }
    public String getTime_Remaining() {
        String s="";
        long f;
        if(current>straight){
                if(IN){f=turn-(current-straight);s+=GO_STRAIGHT;}
            else {s+="Turn Right";
                //TODO Exit
                    f=0;
                }

        }else if(current==straight){
            f=turn;
            s+= Go_LEFT;
        }else {
            if(IN){f=GiveTime(current-straight);s+=GO_STRAIGHT;}
            else f=turn+straight-current;
        }
        return s+"//_//Distance Left:"+f;
    }
}
