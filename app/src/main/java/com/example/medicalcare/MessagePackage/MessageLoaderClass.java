package com.example.medicalcare.MessagePackage;

public class MessageLoaderClass {
    String receiverNumber,message;

    public MessageLoaderClass() {
    }

    public MessageLoaderClass(String receiverNumber, String message) {
        this.receiverNumber = receiverNumber;
        this.message = message;
    }



    public String getReceiverNumber() {
        return receiverNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
