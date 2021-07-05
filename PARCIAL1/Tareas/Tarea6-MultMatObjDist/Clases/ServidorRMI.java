/*
    Autor: Perez Federico Jose Joel
    Fecha: 23-04-2021
    Grupo: 4CM3
    Materia: Desarrollo de Sistemas Distribuidos
    Programa: Multiplicacion De Matrices Utilizando Objetos Distribuidos
 */

import java.rmi.Naming;

public class ServidorRMI {
    
    public static void main(String[] args) throws Exception{
        
        String url = "rmi://localhost/prueba";//Puerto 1099
        ClaseRMI obj = new ClaseRMI();
    
        Naming.rebind(url,obj);
    }
}