package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;


public class Main {
   public static void main(String[] args) throws IOException {
        ArrayList<Integer> board = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0));
        System.out.println("Server avviato. In attesa di giocatori...");
        ServerSocket serverSocket = new ServerSocket(3000);
        Socket s1 = serverSocket.accept();
        BufferedReader in1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
        PrintWriter out1 = new PrintWriter(s1.getOutputStream(), true);
        out1.println("WAIT");
        System.out.println("Giocatore 1 connesso.");
        Socket s2 = serverSocket.accept();
        BufferedReader in2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));
        PrintWriter out2 = new PrintWriter(s2.getOutputStream(), true);
        System.out.println("Giocatore 2 connesso.");
        out1.println("READY");
        out2.println("READY");

        int currentPlayer = 1; 
        boolean gameOver = false;

        while (!gameOver) {
            try {
                BufferedReader in = (currentPlayer == 1) ? in1 : in2;
                PrintWriter out = (currentPlayer == 1) ? out1 : out2;
                PrintWriter outOpp = (currentPlayer == 1) ? out2 : out1;

                String moveStr = in.readLine();
                if (moveStr == null) {
                    outOpp.println("DISCONNECTED");
                    break;
                }

                int move;
                try {
                    move = Integer.parseInt(moveStr);
                } catch (NumberFormatException e) {
                    out.println("KO");
                    continue;
                }
                if (move < 0 || move > 8 || board.get(move) != 0) {
                    out.println("KO");
                    continue;
                }

                board.set(move, currentPlayer);

                if (checkWin(board, currentPlayer)) {
                    out.println("W");
                    outOpp.println(boardToString(board) + "L");
                    gameOver = true;
                    break;
                }

                if (isFull(board)) {
                    out.println("P");
                    outOpp.println(boardToString(board) + "P");
                    gameOver = true;
                    break;
                }

                out.println("OK");
                outOpp.println(boardToString(board));

                currentPlayer = (currentPlayer == 1) ? 2 : 1;

            } catch (IOException e) {
                System.out.println("Un giocatore si è disconnesso.");
                break;
            }
        }

        System.out.println("Partita terminata. Chiudo connessioni...");
        s1.close();
        s2.close();
        serverSocket.close();
    }

    private static String boardToString(ArrayList<Integer> board) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < board.size(); i++) {
            sb.append(board.get(i)).append(",");
        }
        return sb.toString();
    }

    private static boolean isFull(ArrayList<Integer> board) {
        for (int cell : board) {
            if (cell == 0) return false;
        }
        return true;
    }

    private static boolean checkWin(ArrayList<Integer> b, int player) {
        int[][] wins = {
            {0,1,2},{3,4,5},{6,7,8}, 
            {0,3,6},{1,4,7},{2,5,8},
            {0,4,8},{2,4,6}           
        };
        for (int[] combo : wins) {
            if (b.get(combo[0]) == player && b.get(combo[1]) == player && b.get(combo[2]) == player)
                return true;
        }
        return false;
    

   }
}
