/*
    Autor: Perez Federico Jose Joel
    Fecha: 15-03-2021
    Grupo: 4CM3
    Materia: Desarrollo de Sistemas Distribuidos
    Programa: Multiplicacion Distribuidad de Matrices Utilizando paso de Mensajes
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MD {

    static Object lock = new Object();
    
    static int N = 4;

    static int[][] A = new int[N][N];
    static int[][] B = new int[N][N];
    static int[][] C = new int[N][N];

    static int[][] Bt = new int[N][N];

    static int renglon = N / 2;
    static int columna = N;


    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
        while (longitud > 0) {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }

    static class Worker extends Thread {

        Socket conexion;
        byte[] Ai;
        byte[] Bi;

        Worker(Socket conexion, byte[] Ai, byte[] Bi) {
            this.conexion = conexion;
            this.Ai = Ai;
            this.Bi = Bi;
        }

        public void run() {
            try {

                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                
                int tamBuffer = renglon * columna * 4;

                //Enviar Matriz Ai y Bi al nodo i
                salida.write(Ai);
                salida.write(Bi);
                
                //Recibe el numero de nodo i
                int nodo = entrada.readInt();

                //Recibir la matriz Ci del nodo i
                byte[] ci = new byte[tamBuffer];
                read(entrada, ci, 0, tamBuffer);
                ByteBuffer Ci = ByteBuffer.wrap(ci);
                
                
                if(nodo <= 2){
                    int limite=renglon*nodo;
                    int pos=renglon*(nodo-1);
                    for(int i=0; i<renglon; i++)
                        for(int j=pos; j<limite; j++)
                            synchronized(lock)
                            {        
                                C[i][j]=Ci.getInt();
                            }
                }else{
                    int limite=renglon*(nodo-2);
                    int pos=renglon*(nodo-3);
                    for(int i=renglon; i<N; i++)
                        for(int j=pos; j<limite; j++)
                            synchronized(lock)
                            {
                                C[i][j]=Ci.getInt();
                            }
                }

                salida.close();
                entrada.close();

                conexion.close();

            } catch (IOException ex) {
                System.out.println("Error en worker. " + ex);
            } catch (Exception ex) {
                Logger.getLogger(MD.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 1) {
            System.err.println("Uso:");
            System.err.println("java PI <nodo>");
            System.exit(0);
        }

        int nodo = Integer.valueOf(args[0]);

        if (nodo == 0) {//NODO 0
            int tamBuffer = renglon * columna * 4;
            System.out.println("N = " + N);

            //Inicializacion de matrices A, B y C
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    A[i][j] = i - 2 * j;
                    B[i][j] = i + 2 * j;
                    C[i][j] = 0;
                }
            }

            //Imprimir matrices si N=4
            if (N == 4) {
                System.out.println("\nMATRIZ A: ");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        System.out.print(" " + A[i][j]);
                    }
                    System.out.print("\n");
                }

                System.out.println("\nMATRIZ B: ");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        System.out.print(" " + B[i][j]);
                    }
                    System.out.print("\n");
                }
            }

            //Transponer la matriz B en Bt
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    int x = B[i][j];
                    Bt[i][j] = B[j][i];
                    Bt[j][i] = x;
                }
            }

            //Seccionar Matrices A y B
            //Matriz A1
            ByteBuffer a1 = ByteBuffer.allocate(tamBuffer);
            for (int i = 0; i < renglon; i++) {
                for (int j = 0; j < columna; j++) {
                    a1.putInt(A[i][j]);
                }
            }
            byte[] A1 = a1.array();

            //Matriz A2
            ByteBuffer a2 = ByteBuffer.allocate(tamBuffer);
            for (int i = renglon; i < N; i++) {
                for (int j = 0; j < columna; j++) {
                    a2.putInt(A[i][j]);
                }
            }
            byte[] A2 = a2.array();
            
            //Matriz B1
            ByteBuffer b1 = ByteBuffer.allocate(tamBuffer);
            for (int i = 0; i < renglon; i++) {
                for (int j = 0; j < columna; j++) {
                    b1.putInt(Bt[i][j]);
                }
            }
            byte[] B1 = b1.array();

            //Matriz B2
            ByteBuffer b2 = ByteBuffer.allocate(tamBuffer);
            for (int i = renglon; i < N; i++) {
                for (int j = 0; j < columna; j++) {
                    b2.putInt(Bt[i][j]);
                }
            }
            byte[] B2 = b2.array();

            ServerSocket servidor = new ServerSocket(50000);
            Worker[] w = new Worker[4];

            //Enviar Matriz A1 y B1 al nodo 1 
            Socket conexion0 = servidor.accept();
            w[0] = new Worker(conexion0, A1, B1);
            w[0].start();
            
            //Enviar Matriz A1 y B2 al nodo 2
            Socket conexion1 = servidor.accept();
            w[1] = new Worker(conexion1, A1, B2);
            w[1].start();
            
            //Enviar Matriz A2 y B1 al nodo 3
            Socket conexion2 = servidor.accept();
            w[2] = new Worker(conexion2, A2, B1);
            w[2].start();

            //Enviar Matriz A2 y B2 al nodo 4
            Socket conexion = servidor.accept();
            w[3] = new Worker(conexion, A2, B2);
            w[3].start();

            for (int i = 0; i < 4; i++) {
                w[i].join();
            }
            
            if (N == 4) {
                System.out.println("\nMATRIZ C: ");
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        System.out.print(" " + C[i][j]);
                    }
                    System.out.print("\n");
                }
            }
            
            //Clacular el CHECKSUM
            long checksum=0;
            for(int i=0; i<N; i++)
                for(int j=0; j<N; j++)
                    checksum+=C[i][j];

            System.out.println("\nChecksum = " + checksum);

        } else {//NODO 1,2,3,4

            Socket conexion = null;

            for (;;) {
                try {
                    conexion = new Socket("localhost", 50000);
                    break;
                } catch (Exception e) {
                    Thread.sleep(200);
                }
            }
            int tamBuffer = renglon * columna * 4;
            
            try {
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());

                //Recibir del Nodo i la matriz Ai
                byte[] ai = new byte[tamBuffer];
                read(entrada, ai, 0, tamBuffer);
                ByteBuffer Ai = ByteBuffer.wrap(ai);

                //Recibir del Nodo i la matriz Bi
                byte[] bi = new byte[tamBuffer];
                read(entrada, bi, 0, tamBuffer);
                ByteBuffer Bi = ByteBuffer.wrap(bi);
                Bi.array();

                
                int[][] An = new int[renglon][columna];
                int[][] Bn = new int[renglon][columna];
                for (int i = 0; i < renglon; i++) {
                    for (int j = 0; j < columna; j++) {
                        An[i][j] = Ai.getInt();
                        Bn[i][j] = Bi.getInt();
                    }
                }

                //Realizar el producto Ci = Ai x Bi
                ByteBuffer ci = ByteBuffer.allocate(tamBuffer);
                //Multiplicar Matrices
                for (int i = 0; i < renglon; i++) {
                    for (int j = 0; j < renglon; j++) {
                        int ac = 0;
                        for (int k = 0; k < columna; k++) {
                            ac += An[i][k] * Bn[j][k];
                        }
                        ci.putInt(ac);
                    }
                }

                byte[] Ci = ci.array();

                //Enviamos el numero de nodo
                salida.writeInt(nodo);
                //Enviar la matriz Ci al nodo 0
                salida.write(Ci);

                //9-Cerramos los streams de entrada y salida
                salida.close();
                entrada.close();
                //10-Cerrar la conexión
                conexion.close();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

}
