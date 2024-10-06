package com.nice.coday;


import org.apache.poi.ss.formula.eval.NotImplementedException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;


public class ElectricityConsumptionCalculatorImpl implements ElectricityConsumptionCalculator {
    @Override
    public ConsumptionResult calculateElectricityAndTimeConsumption(ResourceInfo resourceInfo) throws IOException {
        // Your implementation will go here
        String line = "";
        String splitBy = ","; // Assuming CSV values are separated by commas
        ArrayList<VehicleTypeDetails> vehicleTypeList=new ArrayList<>();
        List<ConsumptionDetails> consumptionDetailsList=new ArrayList<>();
        Map<String, Long> totalChargingStationTime = new HashMap<>();
        ArrayList<TripDetails> tripDetailsList= new ArrayList<>();
        TreeMap<Double, String> chargingStationInfo = new TreeMap<>();
        HashMap<String, Double> entryExitPointInfo = new HashMap<>();
        HashMap<String, Double> timeToChargeVehicleInfo = new HashMap<>();

        // taking vehicleTypeInfo input
        try (BufferedReader br = new BufferedReader(new FileReader(resourceInfo.getVehicleTypeInfoPath().toString()))) {
            int counter=0;
            while ((line = br.readLine()) != null) {
                // Use comma as separator
                if(counter==0){
                    counter++;
                    continue;
                }
                String[] data = line.split(splitBy);
                vehicleTypeList.add(new VehicleTypeDetails(data[0],data[1],data[2]));
                consumptionDetailsList.add(new ConsumptionDetails(data[0],0d,0l,0l));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        // taking tripDetails input
        try (BufferedReader br = new BufferedReader(new FileReader(resourceInfo.getTripDetailsPath().toString()))) {
            int counter=0;
            while ((line = br.readLine()) != null) {
                // Use comma as separator
                if(counter==0){
                    counter++;
                    continue;
                }
                String[] data = line.split(splitBy);
                tripDetailsList.add(new TripDetails(data[0],data[1],data[2],data[3],data[4]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // taking charging station info input
        try (BufferedReader br = new BufferedReader(new FileReader(resourceInfo.getChargingStationInfoPath().toString()))) {
            int counter=0;
            while ((line = br.readLine()) != null) {
                // Use comma as separator
                if(counter==0){
                    counter++;
                    continue;
                }
                String[] data = line.split(splitBy);
                chargingStationInfo.put(Double.parseDouble(data[1]),data[0]);
                totalChargingStationTime.put(data[0],0l);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // taking entry exit info input
        try (BufferedReader br = new BufferedReader(new FileReader(resourceInfo.getEntryExitPointInfoPath().toString()))) {
            int counter=0;
            while ((line = br.readLine()) != null) {
                // Use comma as separator
                if(counter==0){
                    counter++;
                    continue;
                }
                String[] data = line.split(splitBy);
                entryExitPointInfo.put(data[0],Double.parseDouble(data[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // taking vehicle type and charging station  info input
        try (BufferedReader br = new BufferedReader(new FileReader(resourceInfo.getTimeToChargeVehicleInfoPath().toString()))) {
            int counter=0;
            while ((line = br.readLine()) != null) {
                // Use comma as separator
                if(counter==0){
                    counter++;
                    continue;
                }
                String[] data = line.split(splitBy);
                timeToChargeVehicleInfo.put(data[0]+data[1],Double.parseDouble(data[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(TripDetails currTrip:tripDetailsList){
            double currPointDistance=entryExitPointInfo.get(currTrip.entryPoint);
            currPointDistance= Math.round(currPointDistance*100.0)/100.0;
            double exitPointDistance=entryExitPointInfo.get(currTrip.exitPoint);
            exitPointDistance= Math.round(exitPointDistance*100.0)/100.0;
            String vehicleType= currTrip.vehicleType;
            double remainingBatteryPercentage= currTrip.remainingBatteryPercentage;
            remainingBatteryPercentage= Math.round(remainingBatteryPercentage*100.0)/100.0;
            double mileage=0d;


            double numberOfUnitsForFullyCharge=0d;
            boolean got=false;
            for(VehicleTypeDetails currVehicle:vehicleTypeList){
                if(vehicleType.equals(currVehicle.vehicleType)){
                    mileage=currVehicle.Mileage;
                    numberOfUnitsForFullyCharge=currVehicle.NumberOfUnitsForFullyCharge;
                    got=true;
                    break;
                }
            }
            mileage= Math.round(mileage*100.0)/100.0;
            numberOfUnitsForFullyCharge= Math.round(numberOfUnitsForFullyCharge*100.0)/100.0;
            if(!got) continue;
            double hundred=100.0;

            double remainingBatteryUnits= (remainingBatteryPercentage*numberOfUnitsForFullyCharge)/hundred;
            remainingBatteryUnits= Math.round(remainingBatteryUnits*100.0)/100.0;
            double remainingDistance= (remainingBatteryUnits*mileage)/numberOfUnitsForFullyCharge;
            remainingDistance= Math.round(remainingDistance*100.0)/100.0;
            boolean isvalid=true;
            Double currtotalUnitConsumed=0.0;
            currtotalUnitConsumed= Math.round(currtotalUnitConsumed*100.0)/100.0;


            double currtotalTimeRequired=0.0;
            currtotalTimeRequired= Math.round(currtotalTimeRequired*100.0)/100.0;

            //System.out.println(exitPointDistance+" "+currPointDistance+" "+remainingDistance);
            int counter=0;
            while(exitPointDistance-currPointDistance >= remainingDistance){
                Double maxDist=chargingStationInfo.floorKey(remainingDistance+currPointDistance);
                if(maxDist==null || maxDist<currPointDistance || (maxDist==currPointDistance && counter!=0)){
                    isvalid=false;
                    break;
                }
                counter++;
                maxDist= Math.round(maxDist*100.0)/100.0;
                String currChargingStation=chargingStationInfo.get(maxDist);
                double currCoveredDist=maxDist-currPointDistance;
                currCoveredDist= Math.round(currCoveredDist*100.0)/100.0;
                double currExostedBattery= (currCoveredDist/mileage)*numberOfUnitsForFullyCharge;
                currExostedBattery= Math.round(currExostedBattery*100.0)/100.0;
                double currRequiredBatteryUnits=currExostedBattery+(numberOfUnitsForFullyCharge-remainingBatteryUnits);
                currRequiredBatteryUnits= Math.round(currRequiredBatteryUnits*100.0)/100.0;

                currtotalUnitConsumed+=currRequiredBatteryUnits;
                currtotalUnitConsumed= Math.round(currtotalUnitConsumed*100.0)/100.0;


                double timeToChargePerUnit= timeToChargeVehicleInfo.get(vehicleType+currChargingStation);
                timeToChargePerUnit= Math.round(timeToChargePerUnit*100.0)/100.0;
                currtotalTimeRequired+=timeToChargePerUnit*currRequiredBatteryUnits;
                currtotalTimeRequired= Math.round(currtotalTimeRequired*100.0)/100.0;
                totalChargingStationTime.put(currChargingStation,totalChargingStationTime.get(currChargingStation)+(long)(timeToChargePerUnit*currRequiredBatteryUnits));
                currPointDistance=maxDist;
                remainingDistance=mileage;
                remainingBatteryUnits=numberOfUnitsForFullyCharge;

            }

            for(ConsumptionDetails currConsumptionDeatil: consumptionDetailsList){
                if(vehicleType.equals(currConsumptionDeatil.getVehicleType())){
                    currConsumptionDeatil.setTotalTimeRequired(currConsumptionDeatil.getTotalTimeRequired()+(long)currtotalTimeRequired);
                    currConsumptionDeatil.setTotalUnitConsumed(currConsumptionDeatil.getTotalUnitConsumed()+currtotalUnitConsumed);
                    if(isvalid){
                        currConsumptionDeatil.setNumberOfTripsFinished(1+currConsumptionDeatil.getNumberOfTripsFinished());
                    }
                    break;
                }
            }
        }
        ConsumptionResult result=new ConsumptionResult();
        result.setTotalChargingStationTime(totalChargingStationTime);
        result.setConsumptionDetails(consumptionDetailsList);
        return result;
    }
}
