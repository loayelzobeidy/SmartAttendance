package com.example.almgohar.iottrilaterationproject;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class BeaconService extends Service implements BeaconConsumer {
    private BeaconManager beaconManager;
    private static final String TAG = "BeaconReferenceApp";
    private static ArrayList <String> beaconArray = new ArrayList<String>();
    public static ArrayList<Double> distances = new ArrayList<Double>();
    public long tutorialStartTime = System.currentTimeMillis();
    public long tutorialEndTime = tutorialStartTime + 1000*40;
    public boolean tutorialEnded = false;
    public long startAttendanceTime;
    public boolean isConnected = false;
    public boolean wasConnected = false;
    public String connectedTo = "";
    public Double initialDistance;
    public TextView result;
    int indexOfMinDistances;
    public long timeInTutorialSoFar = 0;

    public BeaconService() {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

        beaconManager.bind(this);
        //TODO do something useful
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            //beaconManager.startRangingBeaconsInRegion(new Region());
        } catch (RemoteException e) {
        }


        beaconManager.setRangeNotifier(new RangeNotifier() {
                                           @Override
                                           public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
                                               Log.i(TAG, "didRangeBeaconsInRegion: ");
                                               Thread thread = new Thread(new Runnable() {
                                                   @Override
                                                   public void run() {
                                                       try {
                                                           if (!tutorialEnded) {
                                                               if (System.currentTimeMillis() >= tutorialEndTime) {
                                                                   tutorialEnded = true;
                                                                   //printout result
                                                                   Context context = getApplicationContext();
                                                                   CharSequence text = "Hello toast!";
                                                                   if(isConnected){
                                                                       timeInTutorialSoFar += System.currentTimeMillis() - startAttendanceTime;
                                                                   }
                                                                   if(timeInTutorialSoFar >= 1000*20){
                                                                       text = "You attended the tutorial and time spent was "+timeInTutorialSoFar/1000;
                                                                   }
                                                                   else
                                                                       text = "You missed the attendance and time spent was "+timeInTutorialSoFar/1000;
                                                                   int duration = Toast.LENGTH_SHORT;

//                                                                   Toast toast = Toast.makeText(context, text, duration);
//                                                                   toast.show();
                                                                   Handler handler = new Handler(Looper.getMainLooper());
                                                                   handler.post(new Runnable() {
                                                                       @Override
                                                                       public void run() {
                                                                           CharSequence text = "Hello toast!";
                                                                           if(timeInTutorialSoFar >= 1000*20){
                                                                               text = "You attended the tutorial and time spent was "+timeInTutorialSoFar/1000;
                                                                               FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                                               DatabaseReference myRef = database.getReference("message");

                                                                               myRef.setValue(text);
                                                                           }
                                                                           else
                                                                               text = "You missed the attendance and time spent was "+timeInTutorialSoFar/1000;
                                                                           int duration = Toast.LENGTH_LONG;
                                                                           Toast toast = Toast.makeText(BeaconService.this.getApplicationContext(), text,duration);
                                                                           toast.show();
                                                                           FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                                           DatabaseReference myRef = database.getReference("message");

                                                                           myRef.setValue(text);


                                                                       }
                                                                   });
                                                               }
                                                               else {

                                                                   if (beacons.size() > 0) {

                                                                       String mac = beacons.iterator().next().getBluetoothAddress();
                                                                       if (!beaconArray.contains(mac)) {
                                                                           beaconArray.add(mac);
                                                                           double distance = beacons.iterator().next().getDistance();
                                                                           final String major = beacons.iterator().next().getId2() + "";
                                                                           final String minor = beacons.iterator().next().getId3() + "";
                                                                           final int rssi = beacons.iterator().next().getRssi();
                                                                           final int power = beacons.iterator().next().getTxPower();

                                                                           //                                                                    for(int i =0;i<3;i++){
                                                                           //                                                                        if(ds[i] == 0.0){
                                                                           //                                                                            double exp = ((power-rssi)/ (10.0*2.0));
                                                                           //                                                                            ds[i] = Math.pow(10.0,exp);
                                                                           //
                                                                           //                                                                        }
                                                                           //                                                                    }
                                                                           distances.add(new Double(distance));
                                                                           Log.i("Major", major);
                                                                           Log.i("Minor", minor);
                                                                           Log.i("RSSI", rssi + "");
                                                                           Log.i("power ", power + "");
                                                                           Log.i("distance", distance + "");
                                                                       }


                                                                   }

                                                                   //                                                           for(int i =0;i<3;i++){
                                                                   //
                                                                   //                                                               Log.i( "DS "+ (i+1) +": ",ds[i]+"");
                                                                   //                                                           }
                                                                   //
                                                                   //                                                           double Va = ((Math.pow(ds[1],2) - Math.pow(ds[2],2))  - (25 - 9) - ( 1 - 25))/2;
                                                                   //                                                           double Vb = ((Math.pow(ds[1],2) - Math.pow(ds[0],2))  - (25 - 1) - ( 1 - 4))/2 ;
                                                                   //                                                           double y = ( (Vb*-2) - (Va*-4))/((1*(-2)) - (4*-4));
                                                                   //                                                           double x = ((Va - y*(4))/-2);
                                                                   //                                                           Log.i("X ",x+"");
                                                                   //                                                           Log.i("Y ",y+"");

                                                                   if (isConnected || wasConnected) {
                                                                       while (beacons.iterator().hasNext()) {
                                                                           Beacon theBeacon = beacons.iterator().next();
                                                                           if (theBeacon.getBluetoothAddress().equals(connectedTo)) {
                                                                               distances.set(indexOfMinDistances, theBeacon.getDistance());
//                                                                           Log.i("newDistance ", theBeacon.getDistance()+"");
                                                                           }
                                                                       }

                                                                   }


                                                                   if (distances.size() >= 1 && isConnected == false && wasConnected == false) {
                                                                       Double minDistance = Collections.min(distances);
                                                                       indexOfMinDistances = distances.indexOf(minDistance);
                                                                       connectedTo = beaconArray.get(indexOfMinDistances);
                                                                       isConnected = true;
                                                                       startAttendanceTime = System.currentTimeMillis();
                                                                       initialDistance = new Double(distances.get(indexOfMinDistances).doubleValue());

                                                                       // print you are now connected to connectedTo on mobile screen
                                                                       Context context = getApplicationContext();
                                                                       CharSequence text = "You are in the tutorial now and time is counting to:  " + connectedTo;
                                                                       int duration = Toast.LENGTH_LONG;
                                                                       Log.i("connected ", text + "");

                                                                       Handler handler = new Handler(Looper.getMainLooper());
                                                                       handler.post(new Runnable() {
                                                                           @Override
                                                                           public void run() {
                                                                               int duration = Toast.LENGTH_LONG;
                                                                               Toast toast = Toast.makeText(BeaconService.this.getApplicationContext(), "You are in the tutorial now and time is counting to:  " + BeaconService.this.connectedTo, duration);
                                                                               toast.show();
                                                                           }
                                                                       });
//

                                                                   }

                                                                   if (isConnected ) {
                                                                       Double newDistance = new Double(distances.get(indexOfMinDistances).doubleValue());
                                                                       if (newDistance > initialDistance + 1) {
                                                                           timeInTutorialSoFar += System.currentTimeMillis() - startAttendanceTime;
                                                                           isConnected = false;
                                                                           wasConnected = true;
                                                                           Context context = getApplicationContext();
                                                                           CharSequence text = "You are out of the tutorial now and time is NOT counting";
                                                                           int duration = Toast.LENGTH_SHORT;
                                                                           Handler handler = new Handler(Looper.getMainLooper());
                                                                           handler.post(new Runnable() {
                                                                               @Override
                                                                               public void run() {
                                                                                   int duration = Toast.LENGTH_LONG;
                                                                                   Toast toast = Toast.makeText(BeaconService.this.getApplicationContext(), "You are out of the tutorial now and time is NOT counting" + BeaconService.this.connectedTo, duration);
                                                                                   toast.show();
                                                                               }
                                                                           });
//
                                                                       }
                                                                   }

                                                                   if (wasConnected && !isConnected) {
                                                                       Double newDistance = new Double(distances.get(indexOfMinDistances).doubleValue());
                                                                       if (newDistance <= initialDistance + 1) {
                                                                           isConnected = true;
                                                                           startAttendanceTime = System.currentTimeMillis();
                                                                           Handler handler = new Handler(Looper.getMainLooper());
                                                                           handler.post(new Runnable() {
                                                                               @Override
                                                                               public void run() {
                                                                                   int duration = Toast.LENGTH_LONG;
                                                                                   Toast toast = Toast.makeText(BeaconService.this.getApplicationContext(), "Reconnecteddd" + BeaconService.this.connectedTo, duration);
                                                                                   toast.show();
                                                                               }
                                                                           });
                                                                       }
                                                                   }
                                                               }

                                                           }

                                                       } catch (Exception e) {
                                                           e.printStackTrace();
                                                       }

                                                   }

                                               });
                                               thread.start();


                                           }
                                       }

        );
        beaconManager.setMonitorNotifier(new

                                                 MonitorNotifier() {
                                                     @Override
                                                     public void didEnterRegion(Region region) {
                                                         Log.i(TAG, "I just saw an beacon for the first time!");
                                                     }

                                                     @Override
                                                     public void didExitRegion(Region region) {
                                                         Log.i(TAG, "I no longer see an beacon");
                                                     }

                                                     @Override
                                                     public void didDetermineStateForRegion(int state, Region region) {
                                                         Log.i(TAG, "I have just switched from seeing/not seeing beacons: " + state);
                                                     }
                                                 }

        );
    }



}
