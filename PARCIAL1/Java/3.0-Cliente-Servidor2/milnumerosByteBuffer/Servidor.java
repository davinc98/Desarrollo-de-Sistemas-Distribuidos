/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package milnumerosByteBuffer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 *
 * @author leoj_
 */
public class Servidor {
        // lee del DataInputStream todos los bytes requeridos
    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
        while (longitud > 0) {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }

    public static void main(String[] args) throws Exception {
        
        System.out.println("Servidor iniciado...");

        ServerSocket servidor = new ServerSocket(50000);

        Socket conexion = servidor.accept();

        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());

        // recibe un entero de 32 bits
        int n = entrada.readInt();
        System.out.println(n);

        // recibe un numero punto flotante
        double x = entrada.readDouble();
        System.out.println(x);

        // recibe una cadena
        byte[] buffer = new byte[4];
        read(entrada, buffer, 0, 4);
        System.out.println(new String(buffer, "UTF-8"));

        // envia una cadena
        salida.write("HOLA".getBytes());

        // recibe 5 numeros punto flotante
        byte[] a = new byte[10000 * 8];
        
        long ms1 = System.currentTimeMillis();
        
        read(entrada, a, 0, 10000 * 8);
        ByteBuffer b = ByteBuffer.wrap(a);
        for (int i = 0; i < 10000; i++) {
            System.out.println(b.getDouble());
        }
        
        long ms2 = System.currentTimeMillis();
        long ms = ms2 -ms1;
        System.out.println("Tiempo de recepcion: "+ms+"ms.");
        
        salida.close();
        entrada.close();
        conexion.close();
    }
}
