package com.nice.coday;

public class VehicleTypeDetails {
    String vehicleType;
    double NumberOfUnitsForFullyCharge;
    double Mileage;
//    double RemainingBatteryPercentage;
    VehicleTypeDetails(String vehicleType, String NumberOfUnitsForFullyCharge,String Mileage){
        this.vehicleType=vehicleType;
        this.NumberOfUnitsForFullyCharge= Double.parseDouble(NumberOfUnitsForFullyCharge);
        this.Mileage= Double.parseDouble(Mileage);
    }
}
