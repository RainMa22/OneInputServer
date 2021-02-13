package me.rainma22.OneInput.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;


public class Main {
    private static ArrayList<String> clients = new ArrayList<>(1);
    private static void sendSignal(String s,DatagramSocket socket){
        DatagramPacket packet;

        for (String client: clients) {
        try {
            packet = new DatagramPacket(s.getBytes(), s.getBytes().length, InetAddress.getByName(client), 2020);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }
    public static void main(String[] args) throws Exception {
        DatagramSocket socket=new DatagramSocket(2021);
        JFrame frame=new JFrame();
        Robot robot=new Robot();
        Toolkit tk=Toolkit.getDefaultToolkit();
        int x=tk.getScreenSize().width/2;
        int y=tk.getScreenSize().height/2;
        robot.mouseMove(x,y);
        frame.setBounds(0,0,x*2,y*2);
        byte[] b=new byte[1];
        Thread t1=new Thread(){
            @Override
            public void run() {
                while (true){
                DatagramPacket verification=new DatagramPacket(b,1);
                try {
                    socket.receive(verification);
                    clients.add(verification.getAddress().toString().substring(1));
                    System.out.println(clients.get(0));
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            }
        };
        t1.start();
        Thread t=new Thread() {
            @Override
            public void run() {
                while(true){
                    int differenceX=x- MouseInfo.getPointerInfo().getLocation().x;
                    int differenceY=y- MouseInfo.getPointerInfo().getLocation().y;
                    String s="MX:"+differenceX+",MY:"+differenceY;
                    robot.mouseMove(x,y);
                    sendSignal(s,socket);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }}
        };
        t.start();
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode()==KeyEvent.VK_ESCAPE) Runtime.getRuntime().exit(0);
                    String s = "KP:" + keyEvent.getKeyCode();
                DatagramPacket packet;
                try {
                    packet = new DatagramPacket(s.getBytes(), s.getBytes().length, InetAddress.getByName(args[0]), 2020);
                    socket.send(packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                String s="KR:"+keyEvent.getKeyCode();
                sendSignal(s,socket);
            }
        });
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                String s="MP:"+e.getButton();
                sendSignal(s,socket);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                String s="MR:"+e.getButton();
                sendSignal(s,socket);
            }

        });
        frame.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                String s="MW:"+e.getWheelRotation();
                sendSignal(s,socket);
            }
        });
        frame.getContentPane().setBackground(new Color(97,97,97,255));
        frame.setVisible(true);
    }
}
