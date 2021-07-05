/*
    Autor: Perez Federico Jose Joel
    Fecha: 19-04-2021
    Grupo: 4CM3
    Materia: Desarrollo de Sistemas Distribuidos
    Programa: Chat Multicast
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.net.MulticastSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;


class Chat{
    
	static void envia_mensaje(byte[] buffer,String ip,int puerto) throws IOException{
		
		DatagramSocket socket = new DatagramSocket();

                socket.send(new DatagramPacket(buffer,buffer.length,InetAddress.getByName(ip),puerto));
		socket.close();
	}
  
	static byte[] recibe_mensaje_multicast(MulticastSocket socket,int longitud_mensaje) throws IOException{
		byte[] buffer = new byte[longitud_mensaje];
		DatagramPacket paquete = new DatagramPacket(buffer,buffer.length);
		socket.receive(paquete);
		return paquete.getData();
	}
  
	static class Worker extends Thread {
		public void run(){
			// En un ciclo infinito se recibirán los mensajes enviados al grupo 
			// 230.0.0.0 a través del puerto 50000 y se desplegarán en la pantalla.
			for(;;){
				try{
					InetAddress grupo = InetAddress.getByName("230.0.0.0");
					MulticastSocket socket = new MulticastSocket(50000);
					socket.joinGroup(grupo);

					byte[] a = recibe_mensaje_multicast(socket,100);
				System.out.print("\t\t");
					System.out.println(new String(a,"Windows-1252"));
					System.out.println("");
					socket.leaveGroup(grupo);
					socket.close();
				}catch(Exception e){
					System.err.println(e.getMessage());
				}
			}
		}
	}

	public static void main(String[] args) throws Exception{
		if (args.length != 1){
		  System.err.println("Se debe pasar como parametros el nombre de usuario");
		  System.exit(1);
		}
		
		Worker w = new Worker();
		w.start();
                
		String nombre_usuario = args[0];
                
        // En un ciclo infinito se leerá cada mensaje del teclado y se enviará el mensaje
		// al grupo 230.0.0.0 a través del puerto 50000.
                
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		
		for(;;){
			try{
				System.out.println("Ingrese el mensaje a enviar: ");  
				String mensaje_ingresado=b.readLine();  
				System.out.println("");
				String mensaje= nombre_usuario+ ": "+ mensaje_ingresado;
				envia_mensaje(mensaje.getBytes(),"230.0.0.0",50000);
			}catch(Exception e){
				System.err.println(e.getMessage());
			}
		}
	}
}