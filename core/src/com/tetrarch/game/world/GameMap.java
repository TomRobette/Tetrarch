package com.tetrarch.game.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tetrarch.game.entities.Entity;

import java.util.HashMap;

public abstract class GameMap {
    protected HashMap<String, Entity> entities;

    public GameMap(){
        entities = new HashMap<>();
    }

    public void render(OrthographicCamera cam, SpriteBatch batch){

        for (Entity entity:entities.values()) {
            entity.render(batch);
        }
    }

    public void update(float delta){
        for (Entity entity : entities.values()) {
            entity.update(delta, -9.8f);
        }
    }

    public void dispose(){

    }

    /****
     * Retourne un tile en fonction de sa position en pixel sur la map
     * @param layer
     * @param x
     * @param y
     * @return
     */
//    public GameTile getTileByLocation(int layer, float x, float y){
//        return this.getTileByCoordinates(layer, Math.round(x/GameTile.TILE_SIZE), Math.round(y/GameTile.TILE_SIZE));
//    }

    public abstract GameTile getTileByLocation(int layer, float x, float y);

    /****
     * Retourne un tile en fonction de ses coordonnées sur la map
     * @param layer
     * @param col
     * @param row
     * @return
     */
    public abstract GameTile getTileByCoordinates(int layer, int col, int row);

    public Boolean doesRectCollideWithMap(float x, float y, float width, float height){
        if (x < 0 || y < 0 || x+width > getPixelWidth() || y+height > getPixelHeight()){
            return true;
        }
        for (int row = (int)(y / GameTile.TILE_SIZE); row < Math.ceil((y + height) / GameTile.TILE_SIZE); row++){
            for (int col = (int)(x / GameTile.TILE_SIZE); col < Math.ceil((x + width) / GameTile.TILE_SIZE); col++){
                for (int layer = 0; layer < getLayers(); layer++){
                    GameTile tile = getTileByCoordinates(layer, col, row);
                    if (tile!=null && tile.isCollidable()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public abstract int getWidth();
    public abstract int getHeight();
    public abstract int getLayers();

    public int getPixelWidth(){
        return this.getWidth()*GameTile.TILE_SIZE;
    }

    public int getPixelHeight(){
        return this.getHeight()*GameTile.TILE_SIZE;
    }

    public void addEntity(String id, Entity entity){
        this.entities.put(id, entity);
    }

    public void removeEntity(String id){
        this.entities.remove(id);
    }

    public Entity getEntity(String id){
        return this.entities.get(id);
    }
}
