package com.mygdx.game.NakamaController;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.heroiclabs.nakama.PermissionRead;
import com.heroiclabs.nakama.PermissionWrite;
import com.heroiclabs.nakama.StorageObjectId;
import com.heroiclabs.nakama.StorageObjectWrite;
import com.heroiclabs.nakama.api.StorageObject;
import com.heroiclabs.nakama.api.StorageObjectAcks;
import com.heroiclabs.nakama.api.StorageObjectList;
import com.heroiclabs.nakama.api.StorageObjects;
import com.mygdx.game.Actors.MyWorld;
import com.mygdx.game.Actors.Objeto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NakamaStorage {
    NakamaSessionManager nakamaSessionManager;
    Json json1 = new Json();


    public NakamaStorage(NakamaSessionManager nakamaSessionManager) {
        this.nakamaSessionManager = nakamaSessionManager;
    }

    public void crearObjeto(String nombre, float precio, String descripcion){
        String data = "{\"nombre\" : \""+nombre+"\", "+"\"precio\": \""+precio+"\","+"\"descripcion\" : \""+descripcion+"\""+"}";
        StorageObjectWrite saveGameObject = new StorageObjectWrite("Objetos", nombre, data, PermissionRead.PUBLIC_READ, PermissionWrite.OWNER_WRITE);
        StorageObjectAcks acks = null;
        try {
            acks = nakamaSessionManager.client.writeStorageObjects(nakamaSessionManager.session, saveGameObject).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (acks != null){
            System.out.format("Stored objects %s", acks.getAcksList());
        }
    }

    public List<Objeto> getObjetosTienda(){
        List<Objeto> objetosTienda = new ArrayList<>();
        StorageObjectList objects = null;
        String objetoJson;
        try {
            objects = nakamaSessionManager.client.listUsersStorageObjects(nakamaSessionManager.session, "Objetos", null,100).get();
            for (StorageObject object: objects.getObjectsList()){
                objetoJson = object.getValue();
                objetosTienda.add(jsonToObjeto(objetoJson));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (objects != null){

            return objetosTienda;
        }else return null;

    }

    public List<Objeto> getObjetosInventario(){
        List<Objeto> objetosInvetario = new ArrayList<>();
        StorageObjectList objects = null;
        String objetoJson;
        try {
            objects = nakamaSessionManager.client.listUsersStorageObjects(nakamaSessionManager.session, "Inventario", nakamaSessionManager.session.getUserId(),100).get();
            for (StorageObject object: objects.getObjectsList()){
                objetoJson = object.getValue();
                objetosInvetario.add(jsonToObjeto(objetoJson));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (objects != null){

            return objetosInvetario;
        }else return null;
    }

    public Objeto comprarObjeto(String nombre){
        for (Objeto objeto: getObjetosTienda()){
            if (objeto.getNombre().equals(nombre)){
                String data = "{\"nombre\" : \""+objeto.getNombre()+"\", "+"\"descripcion\": \""+objeto.getDescripcion()+"\","+"\"precio\" : \""+objeto.getPrecio()+"\""+"}";
                StorageObjectWrite saveGameObject = new StorageObjectWrite("Inventario", objeto.getNombre(), data, PermissionRead.OWNER_READ, PermissionWrite.OWNER_WRITE);
                StorageObjectAcks acks = null;
                try {
                    acks = nakamaSessionManager.client.writeStorageObjects(nakamaSessionManager.session, saveGameObject).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                if (acks != null){
                    System.out.format("Stored objects %s", acks.getAcksList());
                    return new Objeto(objeto.getNombre(),objeto.getPrecio(),objeto.getDescripcion());
                }
            }
        }
        return null;
    }

    public Objeto jsonToObjeto(String objeto){
        try {
            return json1.fromJson(Objeto.class,objeto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Vector2 getPosicionJugador(){
        StorageObjectId objectId = new StorageObjectId("player_data");
        objectId.setKey("position_User");
        objectId.setUserId(nakamaSessionManager.session.getUserId());
        StorageObjects objects = null;
        Vector2 posicion = null;
        try {
             objects= nakamaSessionManager.client.readStorageObjects(nakamaSessionManager.session, objectId).get();
            PosicionInicio posicionInicio = json1.fromJson(PosicionInicio.class,objects.getObjects(0).getValue());
            System.out.println(posicionInicio);
            float x = posicionInicio.x;
            float y = posicionInicio.y;
            MyWorld world = nakamaSessionManager.getMyWorld();
            world.personaje.setPosition(x,y);
            world.personaje.body.setTransform(x,y,0);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return posicion;
    }

    public boolean eliminarObjetoDelInventario(Objeto objeto) {
        System.out.println(objeto.toString());
        StorageObjectId objectId = new StorageObjectId("Inventario");
        objectId.setKey(objeto.getNombre());
        objectId.setUserId(nakamaSessionManager.session.getUserId());
        String version = null;
        try {
            StorageObjects objetoInventario = nakamaSessionManager.client.readStorageObjects(nakamaSessionManager.session,objectId).get();
            if (objetoInventario.getObjectsCount() == 0){
                return false;
            }else {
                StorageObject stObjct = objetoInventario.getObjects(0);
                version = stObjct.getVersion();
            }
        } catch (InterruptedException | ExecutionException e) {
            System.out.println();
        }

        objectId.setVersion(version);
        try {
            nakamaSessionManager.client.deleteStorageObjects(nakamaSessionManager.session, objectId).get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println();
        }
        System.out.format("Objeto: "+objeto.getNombre()+" borrado.");
        return false;
    }
}