
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author yz
 */
public class HiloServidor implements Runnable{
    //Declaramos las variables que utiliza el hilo para estar recibiendo y mandando mensajes
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    //Lista de los usuarios conectados al servidor
    private LinkedList<Socket> usuarios = new LinkedList<Socket>();
    private PlayerScore ps = new PlayerScore();
    private int contPlay;
    private ArrayList<PlayerScore> pl= new ArrayList<PlayerScore>();

//Constructor que recibe el socket que atendera el hilo y la lista de usuarios conectados
    public HiloServidor(Socket soc,LinkedList users, int cont, ArrayList ps){
        socket = soc;
        usuarios = users;
        this.contPlay = cont;
        
    }

    public PlayerScore getPs() {
        return ps;
    }
    
    
    @Override
    public void run() {
        try {
            //Inicializamos los canales de comunicacion y mandamos un mensaje de bienvenida
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("<h2>Bienvenido....</h2>");
            //System.err.println(in.readUTF());
            //ps = new PlayerScore(contPlay, in.readUTF());
            ps.setCodPlayer(contPlay);
            ps.setNombrePlayer(in.readUTF());
            //System.out.println("usuario: "+ps.getNombrePlayer());
            if(contPlay>=2){
                for (int i = 0; i < usuarios.size(); i++) {
                    out = new DataOutputStream(usuarios.get(i).getOutputStream());
                    out.writeUTF("start");
                }
            }
            //Ciclo infinito para escuchar por mensajes del cliente
            while(true){
               String recibido = in.readUTF();
               if(recibido.equals("addCoin")){
                   ps.addScore(1);
               }
               //Cuando se recibe un mensaje se envia a todos los usuarios conectados 
                for (int i = 0; i < usuarios.size(); i++) {
                    out = new DataOutputStream(usuarios.get(i).getOutputStream());
                    out.writeUTF(recibido);
                }
            }
        } catch (IOException e) {
            //Si ocurre un excepcion lo mas seguro es que sea por que el cliente se desconecto asi que lo quitamos de la lista de conectados
            for (int i = 0; i < usuarios.size(); i++) {
                if(usuarios.get(i) == socket){
                    usuarios.remove(i);
                    break;
                } 
            }
        }
    }
}
