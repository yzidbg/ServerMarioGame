
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
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
    private boolean forward=true;
    private int playersEnded=0;
    private Map mapaResultados= new HashMap();

//Constructor que recibe el socket que atendera el hilo y la lista de usuarios conectados
    public HiloServidor(Socket soc,LinkedList users, int cont, Map mr){
        socket = soc;
        usuarios = users;
        this.contPlay = cont;
        this.mapaResultados=mr;
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
            ps.setCodPlayer(contPlay);
            ps.setNombrePlayer(in.readUTF());
            if(contPlay>=2){
                for (int i = 0; i < usuarios.size(); i++) {
                    out = new DataOutputStream(usuarios.get(i).getOutputStream());
                    out.writeUTF("start");
                }
            }
            //Ciclo infinito para escuchar por mensajes del cliente
            while(true){
                forward=true;
                String recibido = in.readUTF();
                String filtro=recibido.substring(0,7);
                switch (filtro){
                    case "addCoin":
                        ps.addScore(1);
                        forward=false;
                       
                    case "endGame":
                        recibido = recibido.substring(7);
                        StringTokenizer st = new StringTokenizer(recibido, ":");
                        String k=st.nextToken();
                        String n=st.nextToken();
                        mapaResultados.put(Integer.parseInt(k),n);
                        if(mapaResultados.size()>=2){
                            recibido=msgSalida();
                        }else{
                            forward=false;
                        }
                }
                //Cuando se recibe un mensaje se envia a todos los usuarios conectados 
                if(forward==true){
                    for (int i = 0; i < usuarios.size(); i++) {
                        out = new DataOutputStream(usuarios.get(i).getOutputStream());
                        out.writeUTF(recibido);
                    }
                }
            }
        }catch (IOException e) {
            //Si ocurre un excepcion lo mas seguro es que sea por que el cliente se desconecto asi que lo quitamos de la lista de conectados
            for (int i = 0; i < usuarios.size(); i++) {
                if(usuarios.get(i) == socket){
                    usuarios.remove(i);
                    break;
                }
            }
        }
    }
    private String msgSalida(){
        TreeMap sHM = new TreeMap(mapaResultados);
        String r= "El jugador "+sHM.lastEntry().getValue()+" ha ganado con "
                +sHM.lastEntry().getKey()+" puntos";
        return r;
    }

}
