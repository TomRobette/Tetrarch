package com.tetrarch.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.tetrarch.game.entities.Entity;

public class TiledGameMap extends GameMap {
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    public TiledGameMap(){
        this.tiledMap = new TmxMapLoader().load("maps/black.tmx");
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

    }

    @Override
    public void render(OrthographicCamera cam, SpriteBatch batch) {
        tiledMapRenderer.setView(cam);
        tiledMapRenderer.render();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        super.render(cam, batch);
        batch.end();
    }

    @Override
    public void update(float delta) {
        for (Entity entity : entities.values()) {
            entity.update(delta, -9.8f);
        }
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
    }

    @Override
    public GameTile getTileByLocation(int layer, float x, float y) {
        return null;
    }

    @Override
    public GameTile getTileByCoordinates(int layer, int col, int row) {
        TiledMapTileLayer.Cell cell = ((TiledMapTileLayer) tiledMap.getLayers().get(layer)).getCell(col, row);
        if (cell!=null){
            TiledMapTile tile= cell.getTile();
            if (tile!=null){
                int id = tile.getId();
                return GameTile.getTileById(id);
            }
        }
        return null;
    }

    @Override
    public int getWidth() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getWidth();
    }

    @Override
    public int getHeight() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getHeight();
    }

    @Override
    public int getLayers() {
        return tiledMap.getLayers().getCount();
    }
}
