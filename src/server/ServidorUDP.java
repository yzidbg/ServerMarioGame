package server;

import java.io.*;   
import java.net.*;   
import java.util.ArrayList;


public class ServidorUDP {
    private DatagramSocket socket;
    private ArrayList<InetAddress> clientes = new ArrayList<>();
    // configurar GUI y DatagramSocket   
    public ServidorUDP(){   
        // crear objeto DatagramSocket para enviar y recibir paquetes   
        try {
            System.err.println("Servidor Corriendo");
            socket = new DatagramSocket( 5000 );   
        }   
        // procesar los problemas que pueden ocurrir al crear el objeto DatagramSocket   
        catch( SocketException excepcionSocket ) {   
            excepcionSocket.printStackTrace();   
        }   
    } // fin del constructor de Servidor   
    // esperar a que lleguen los paquetes, mostrar los datos y repetir el paquete al cliente   
    private void esperarPaquetes(){
        while ( true ) { // iterar infinitamente   
            // recibir paquete, mostrar su contenido, devolver copia al cliente   
            try {   
                // establecer el paquete   
                byte datos[] = new byte[ 100 ];   
                DatagramPacket recibirPaquete = new DatagramPacket( datos, datos.length );   
                socket.receive(recibirPaquete); // esperar el paquete 
                InetAddress dir = recibirPaquete.getAddress();
                if(buscarCliente(dir)==false){
                    clientes.add(recibirPaquete.getAddress());
                }
                System.out.println("dirs "+clientes.toString());
                //  mostrar la información del paquete recibido   
                mostrarMensaje( "\nPaquete recibido:" +   
                        "\nDel host: " + recibirPaquete.getAddress() +   
                        "\nPuerto del host: " + recibirPaquete.getPort() +   
                        "\nLongitud: " + recibirPaquete.getLength() +   
                        "\nContenido:\n\t" + new String( recibirPaquete.getData(),   
                        0, recibirPaquete.getLength() ) );   
                enviarPaqueteACliente( recibirPaquete ); // enviar paquete al cliente   
            }catch( IOException excepcionES ) { // procesar los problemas que pueden ocurrir al manipular el paquete   
                mostrarMensaje( excepcionES.toString() + "\n" );   
                excepcionES.printStackTrace();   
            }       
        } // fin de instrucción while   
    } // fin del método esperarPaquetes   
    // repetir el paquete al cliente   
    private void enviarPaqueteACliente(DatagramPacket packRec)throws IOException{
        mostrarMensaje( "\n\nRepitiendo datos al cliente..." );
        for(int i=0; i<clientes.size();i++){
            DatagramPacket enviarPaquete = new DatagramPacket(
                packRec.getData(), packRec.getLength(),   
                clientes.get(i), packRec.getPort() );   
            socket.send( enviarPaquete ); // enviar el paquete   
        
        }
           
        // crear paquete a enviar   
        mostrarMensaje( "Paquete enviado\n" );   
    }   
    // método utilitario que es llamado desde otros subprocesos para manipular a   
    // areaPantalla en el subproceso despachador de eventos   
    private void mostrarMensaje( final String mensajeAMostrar ){
        // mostrar el mensaje del subproceso de ejecución despachador de eventos  
        System.out.println("MensajeEntrante: "+mensajeAMostrar);
    }   
    
    private boolean buscarCliente(InetAddress dir){
        boolean r=false;
        String s = new String();
        s=dir.toString();
        for(int i=0; i<clientes.size();i++){
            if(s.equals(clientes.get(i).toString())){
                System.err.print("entra");
                r=true;
                break;
            }
        }
        return r;
    }
    
    public static void main( String args[] ){
        ServidorUDP aplicacion = new ServidorUDP();   
        aplicacion.esperarPaquetes();   
    }


} // fin de la clase Servidor  
