package com.cafateria.services;

import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.cafateria.database.PrefManager;
import com.cafateria.helper.AdminHelper;
import com.cafateria.model.Order;

import java.util.List;

public class AdminService extends Service {
    private static int LAST_ORDER_ID = 0;
    private boolean isChecking = false;
    private static final String TAG = "AdminService";
    private Dialog dialog;

    public AdminService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        startServices();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startServices() {

        final int inerval = PrefManager.getLessQuantityInterval(AdminService.this);
        Scheduler.start(1, inerval - 1, new Scheduler.ScheduleCallBack() {
            @Override
            public void call() {//
                AdminHelper.checkLessQuantity(AdminService.this);
            }
        });
    }


    private void showOrdersNotification(List<Order> lastOrders) {
        //NotificationHelper.showNotification(this);
    }
}