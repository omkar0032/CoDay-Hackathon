package com.nice.coday;

public class TripDetails {
    double id;
    String vehicleType;
    double remainingBatteryPercentage;
    String entryPoint;
    String exitPoint;
    TripDetails(String id, String vehicleType,String remainingBatteryPercentage,String entryPoint,String exitPoint  ){
        this.id= Double.parseDouble(id);
        this.vehicleType=vehicleType;
        this.remainingBatteryPercentage= Double.parseDouble(remainingBatteryPercentage);
        this.entryPoint=entryPoint;
        this.exitPoint=exitPoint;
    }
}
