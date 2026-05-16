package net.grongubbe.tengoku.client;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("Hello Client!");
        Tengoku tengoku = new Tengoku();
        
        tengoku.run();
    }
}
