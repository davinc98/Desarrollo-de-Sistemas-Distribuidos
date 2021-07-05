/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
    Autor: Jose Perez
    Fecha: 13-03-2021
    Programa: Multiplicacion Dsitribuida de Matrices Utilizando Paso de Mensajes
 */
public class MultiplicaMatriz2 {
    static int N=1000;
    static int [][] A = new int[N][N];
    static int [][] B = new int[N][N];
    static int [][] C = new int[N][N];
            
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        
        //Inicializacion de matrices A y B
        for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                A[i][j]=2*i-j;
                B[i][j]=i+2*j;
                C[i][j]=0;
            }
        }
        
        //Transponer la matriz B
        for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                int x = B[i][j];
                B[i][j] = B[j][i];
                B[j][i] = x;
            }
        }
        
        //Multiplicar Matrices
        for(int i=0;i<N;i++)
            for(int j=0;j<N;j++)
                for(int k=0;k<N;k++)
                    C[i][j]+=A[i][k]*B[j][k];
        
        long t2 = System.currentTimeMillis();
        System.out.println("Tiempo: "+(t2-t1)+"ms");
    }
    
}
