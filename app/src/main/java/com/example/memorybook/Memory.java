package com.example.memorybook;

public class Memory {

    private String name;
    private int id;
    public Memory (String name , int id){
        this.name = name;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public int getId() {
        return id;
    }
}
