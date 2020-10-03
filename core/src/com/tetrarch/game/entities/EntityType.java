package com.tetrarch.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.tetrarch.game.world.GameMap;

import java.util.HashMap;

public enum EntityType {

    PLAYER("player",32, 32, 40);

    private String id;
    private int height, width;
    private float weight;

    private EntityType(String id, int width, int height, float weight){
        this.id = id;
        this.height = height;
        this.width = width;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public float getWeight() {
        return weight;
    }

    private static HashMap<String, EntityType> entityTypes;

    static {
        entityTypes = new HashMap<String, EntityType>();
        for (EntityType type : EntityType.values()){
            entityTypes.put(type.id, type);
        }
    }
}
