/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package milnumerosWriteDouble;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 *
 * @author leoj_
 */
public class Cliente {
    // lee del DataInputStream todos los bytes requeridos
    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
        while (longitud > 0) {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }

    public static void main(String[] args) throws IOException, Exception {
        Socket conexion = new Socket("localhost", 50000);

        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());

        // enva un entero de 32 bits
        salida.writeInt(123);

        // envia un numero punto flotante
        salida.writeDouble(1234567890.1234567890);

        // envia una cadena
        salida.write("hola".getBytes());

        // recibe una cadena
        byte[] buffer = new byte[4];
        read(entrada, buffer, 0, 4);
        System.out.println(new String(buffer, "UTF-8"));

        long ms1 = System.currentTimeMillis();
        
            // envia 1000 numeros punto flotante
            Double num = 0.0;
            for(int i=0; i<10000; i++){
                num = num + 1.0;
                salida.writeDouble(num);            
            }
        
        long ms2 = System.currentTimeMillis();
        long ms = ms2 -ms1;
        System.out.println("Tiempo de envio: "+ms+"ms.");
        
        salida.close();
        entrada.close();
        conexion.close();
    }
    
}
