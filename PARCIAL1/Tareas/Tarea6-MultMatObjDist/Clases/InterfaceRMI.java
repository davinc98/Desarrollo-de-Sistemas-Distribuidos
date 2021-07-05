/*
    Autor: Perez Federico Jose Joel
    Fecha: 23-04-2021
    Grupo: 4CM3
    Materia: Desarrollo de Sistemas Distribuidos
    Programa: Multiplicacion De Matrices Utilizando Objetos Distribuidos
 */

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface InterfaceRMI extends Remote{
    //Prototipo
    public float [][] multiplica_matrices(float [][] A, float [][] B, int N) throws RemoteException;
}