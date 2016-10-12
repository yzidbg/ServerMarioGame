package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JOptionPane;

public class Servidor {
    //Inicializamos el puerto y el numero maximo de conexciones que acepta el servidor
    private final int puerto = 2027;
    private final int noConexiones = 20;
    //Creamos una lista de sockets, donde guardaremos los sockets que se vayan conectando
    private LinkedList<Socket> clientes = new LinkedList<Socket>();
    private int numCli=1;
    private ArrayList<PlayerScore> arr= new ArrayList<PlayerScore>();
    private boolean endGame=false;
       
   //Funcion para que el servidor empieze a recibir conexiones de clientes
    public void escuchar(){
        try {
            //Creamos el socket servidor
            ServerSocket servidor = new ServerSocket(puerto,noConexiones);
            //Ciclo infinito para estar escuchando por nuevos clientes
            //while(numCli<=2){
            while(true){
                System.out.println("Escuchando...."+(numCli));
                //Cuando un cliente se conecte guardamos el socket en nuestra lista
                Socket cliente = servidor.accept();
                clientes.add(cliente);
                //Instanciamos un hilo que estara atendiendo al cliente y lo ponemos a escuchar
                Runnable  run = new HiloServidor(cliente, clientes, numCli, arr);
                Thread hilo = new Thread(run);
                hilo.start();
                //arr.add(run.getPs());
                
                numCli++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //public void 
    }
    
    //Funcion main para correr el servidor
    public static void main(String[] args) {
        Servidor servidor= new Servidor();
        servidor.escuchar();
    }
}
