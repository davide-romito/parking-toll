package com.library.parkingtoll.service.parking.exception;

public class ParkingAlreadyExistException extends Exception {
    public ParkingAlreadyExistException(String message) {
        super(message);
    }
}
