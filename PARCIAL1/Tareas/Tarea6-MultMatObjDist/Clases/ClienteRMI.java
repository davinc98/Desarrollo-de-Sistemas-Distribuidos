/*
    Autor: Perez Federico Jose Joel
    Fecha: 23-04-2021
    Grupo: 4CM3
    Materia: Desarrollo de Sistemas Distribuidos
    Programa: Multiplicacion De Matrices Utilizando Objetos Distribuidos
 */

import java.rmi.Naming;

public class ClienteRMI {

    static final int N = 8;

    static float[][] A = new float[N][N];
    static float[][] B = new float[N][N];
    static float[][] C = new float[N][N];
    
    static float[][] A1;
    static float[][] A2;
    static float[][] B1;
    static float[][] B2;

    static float[][] C1;
    static float[][] C2;
    static float[][] C3;
    static float[][] C4;
    
    public static void main(String[] args) throws Exception {
        
        //Puerto default 1099
        String url1 = "rmi://10.0.0.5/prueba";
        String url2 = "rmi://10.0.0.6/prueba";
        
        // Inicializacion de matrices
        for (int i = 0; i < N; i++){
            for (int j = 0; j < N; j++){
                A[i][j] = i - 2 * j;
                B[i][j] = i + 2 * j;               
            }
        } 
                
        //Transponer la matriz B
        for (int i = 0; i < N; i++){
            for (int j = 0; j < i; j++){
                float t = B[i][j];
                B[i][j] = B[j][i];
                B[j][i] = t;
            }
        }
        
        InterfaceRMI r1 = (InterfaceRMI)Naming.lookup(url1);
        InterfaceRMI r2 = (InterfaceRMI)Naming.lookup(url2);

        A1 = separa_matriz(A, 0);
        A2 = separa_matriz(A, N/2);
        B1 = separa_matriz(B, 0); 
        B2 = separa_matriz(B, N/2);

        C1 = r1.multiplica_matrices(A1, B1, N);
        C2 = r1.multiplica_matrices(A1, B2, N);
        C3 = r2.multiplica_matrices(A2, B1, N);
        C4 = r2.multiplica_matrices(A2, B2, N);

        acomoda_matriz(C, C1, 0, 0);
        acomoda_matriz(C, C2, 0, N/2);
        acomoda_matriz(C, C3, N/2, 0);
        acomoda_matriz(C, C4, N/2, N/2);
        
        if (N == 8){
            imprime_matriz(A, N, N, "A");
            imprime_matriz(B, N, N, "B Transpuesta");

            imprime_matriz(C, N, N, "C");
        } 
        
        //Calcular Checksum
        double checksum=0;
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                checksum += C[i][j];
        
        System.out.println("\tCHECKSUM = " + checksum);
        
    }    

    static void acomoda_matriz (float [][] C,float [][] A, int renglon, int columna) {
        for (int i = 0; i < N/2; i++)
            for (int j = 0; j < N/2; j++)
                C[i + renglon][j + columna] = A[i][j];
    }

    static float [][] separa_matriz(float [][] A,int inicio) {
        float [][] M = new float [N/2][N];
        for (int i = 0; i < N/2; i++)
            for (int j = 0; j < N; j++)
                M[i][j] = A[i + inicio][j];
        return M;    
    } 
    
    static void imprime_matriz(float[][] m, int filas, int columnas, String s) {
        System.out.println("\nMATRIZ " + s);
        for (int i = 0; i< filas; i++){
            for (int j = 0; j < columnas; j++){
                System.out.print(m[i][j] + " ");
            }
            System.out.println("");
        }
    }
}