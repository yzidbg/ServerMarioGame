package Server;

import java.io.*;   
import java.net.*;   
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;


public class ServidorUDP {
    private DatagramSocket socket;
    private ArrayList<InetAddress> clientes = new ArrayList<>();
    private Map hm= new HashMap();
    private Map mapaResultados= new HashMap();
    // configurar GUI y DatagramSocket   
    public ServidorUDP(){   
        // crear objeto DatagramSocket para enviar y recibir paquetes   
        try {
            System.err.println("Servidor Corriendo");
            socket = new DatagramSocket(5000);   
        }   
        // procesar los problemas que pueden ocurrir al crear el objeto DatagramSocket   
        catch( SocketException excepcionSocket ) {   
            excepcionSocket.printStackTrace();   
        }   
    } // fin del constructor de Servidor   
    // esperar a que lleguen los paquetes, mostrar los datos y repetir el paquete al cliente   
    private void esperarPaquetes(){
        while(true) { // iterar infinitamente   
            // recibir paquete, mostrar su contenido, devolver copia al cliente   
            try {   
                // establecer el paquete   
                byte datos[] = new byte[ 100 ];   
                DatagramPacket recibirPaquete = new DatagramPacket( datos, datos.length );   
                socket.receive(recibirPaquete); // esperar el paquete 
                InetAddress dir = recibirPaquete.getAddress();
                int port = recibirPaquete.getPort();
                String recibido = new String( recibirPaquete.getData(), 
                        0, recibirPaquete.getLength());
                if(!hm.containsKey(port)){
                    hm.put(port, dir);
                }
                if(hm.size()>=2){
                    enviarPack("start");
                }if(recibido.length()>=7){
                    String filtro=recibido.substring(0,7);
                    switch (filtro){
                        case "endGame":
                            recibido = recibido.substring(7);
                            StringTokenizer st = new StringTokenizer(recibido, ":");
                            String k=st.nextToken();
                            String n=st.nextToken();
                            mapaResultados.put(Integer.parseInt(k),n);
                            if(mapaResultados.size()>=2){
                                enviarPack(msgSalida());
                            }
                    }
                }
                
            }catch( IOException excepcionES ) { // procesar los problemas que pueden ocurrir al manipular el paquete   
                mostrarMensaje( excepcionES.toString() + "\n" );   
                excepcionES.printStackTrace();   
            }       
        } // fin de instrucci�n while   
    } // fin del m�todo esperarPaquetes   
    
    private void enviarPack(String msg) throws IOException{
        byte datos[] = msg.getBytes();
        Iterator it = hm.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry)it.next();
            DatagramPacket enviarPaquete = new DatagramPacket(
                datos, datos.length,(InetAddress)e.getValue(), 
                    (int)e.getKey());   
            socket.send( enviarPaquete ); // enviar el paquete   
        }
        
        mostrarMensaje( "Paquete enviado: "+ msg+"\n" ); 
    }
    // m�todo utilitario que es llamado desde otros subprocesos para manipular a   
    // areaPantalla en el subproceso despachador de eventos   
    private void mostrarMensaje( final String mensajeAMostrar ){
        // mostrar el mensaje del subproceso de ejecuci�n despachador de eventos  
        System.out.println("MensajeSaliente: "+mensajeAMostrar);
    }  
    
     private String msgSalida(){
        TreeMap sHM = new TreeMap(mapaResultados);
        String r= "El jugador "+sHM.lastEntry().getValue()+" ha ganado con "
                +sHM.lastEntry().getKey()+" puntos";
        return r;
    }
    /*
    public static void main( String args[] ){
        ServidorUDP aplicacion = new ServidorUDP();   
        aplicacion.esperarPaquetes();   
    }*/


} // fin de la clase Servidor  
