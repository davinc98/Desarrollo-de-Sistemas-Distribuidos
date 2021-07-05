/*
    Autor: Perez Federico Jose Joel
    Fecha: 23-03-2021
    Grupo: 4CM3
    Materia: Desarrollo de Sistemas Distribuidos
    Programa: Implementacion de un token-ring.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TokenRing {

    static DataInputStream entrada;
    static DataOutputStream salida;
    static boolean primera_vez = true;
    static String ip;
    static int nodo;
    static int token;
    static int contador = 0;

    static class Worker extends Thread {

        public void run() {
            try {
                //Algoritmo 1
                
                ServerSocket servidor = new ServerSocket(50000);

                Socket conexion = servidor.accept();

                entrada = new DataInputStream(conexion.getInputStream());

            } catch (IOException ex) {
                System.out.println("Error en run:" + ex);
            }

        }
    }

    public static void main(String[] args) throws Exception{

        if (args.length != 2) {
            System.err.println("Se debe pasar como parametros el numero del nodo y la IP del siguiente nodo.");
            System.exit(1);
        }

        nodo = Integer.valueOf(args[0]);
        ip = args[1];

        //Algoritmo 2
        Worker w = new Worker();
        w.start();

        Socket conexion = null;

        for (;;) {
            try {
                conexion = new Socket(ip, 50000);
                break;
            } catch (Exception e) {
                Thread.sleep(500);
            }
        }
        
        try {
            salida = new DataOutputStream(conexion.getOutputStream());
            w.join();
            
            for(;;){
                if(nodo==0){
                                        
                    if(primera_vez==true){
                        primera_vez=false;
                        token=1;
                    }else{
                        token = entrada.readInt();
                        contador++;   
                        
                        System.out.println(" ");
                        System.out.print("\tNodo: "+nodo);
                        System.out.print("\tContador: "+contador);
                        System.out.print("\tToken: "+token);
                    }
                    
                    if(contador==1000)
                        break;
                    
                }else{
                    token = entrada.readInt();
                    contador++;
                    System.out.println(" ");
                    System.out.print("\tNodo: "+nodo);
                    System.out.print("\tContador: "+contador);
                    System.out.print("\tToken: "+token);
                
                }
                
                salida.writeInt(token);
                
            }
            
        } catch (IOException ex) {
            Logger.getLogger(TokenRing.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
