package code.common;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

/**
 * Created by shohrab on 5/31/2016.
 */
public class BroadCastService extends Service {


    private static final String TAG = "BroadcastService";
    public static final String BROADCAST_ACTION = "ASD";
    private final Handler handler = new Handler();
    Intent intent;
    private static double changePositionBy = 0.00005;

    double latitude =0;

    // Getting longitude of the current location
    double longitude =0;

    @Override
    public void onCreate() {
        super.onCreate();

        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 180000); // 1 second

        return super.onStartCommand(intent, flags, startId);
    }


    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            broadcastUpdateInfo();
            handler.postDelayed(this, 180000); // broadcast in every 10 seconds
        }
    };

    //call your API in this method
    private void broadcastUpdateInfo() {
        //Log.d(TAG, "entered DisplayLoggingInfo");

        /*//increment position(lat,lng) by 0.000005
        changePositionBy = changePositionBy + 0.000005;

        ArrayList<CarParcelable> carParcelableList = new ArrayList<CarParcelable>();

        //Here I have incremented lat and long by 0.00005 in every 10 seconds. In real scenario you
        //should fetch current location data from your web service.
        *//*for(ApiResponse.PlaceMarks car : MainActivityPresenter.sCarList){
            carParcelableList.add(new CarParcelable(car.getCoordiatesList().get(0)+changePositionBy, car.getCoordiatesList().get(1)+changePositionBy, car.getCarName(), car.getAddress(),1));
        }*//*

        carParcelableList.add(new CarParcelable(latitude+ changePositionBy, longitude+ changePositionBy, "Car", "Data",1));

        intent.putParcelableArrayListExtra("carList", carParcelableList);*/
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
    }

}
