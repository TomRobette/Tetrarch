package com.tetrarch.game.world;

import java.util.HashMap;

public enum GameTile {
    BLOCK(1, true, "Block", 0),
    ULCB(2, true, "ULCB",0),
    UB(3, true, "UB",0),
    URCB(4, true, "URCG",0),
    DLCB(5, true, "DLCB",0),
    DB(6, true, "DB",0),
    DRCB(7, true, "DRCB",0),
    LAMP(8, true, "Lamp",0),
    PLANT1(9, false, "Plant1",0),
    PLANT2(10, false, "Plant2",0),
    SKY(11, false, "Sky",0),
    DPILLAR(12, false, "DPILLAR",0),
    PILLAR(13, false, "Pillar",0),
    DBPILLAR(14, false, "DBPILLAR",0),
    UBPILLAR(15, false, "UBPILLAR",0),
    UPILLAR(16, false, "UPILLAR",0);

    public final static int TILE_SIZE = 16;

    private Integer id;
    private Boolean collidable;
    private String name;
    private Integer damage;

    private GameTile(Integer id, Boolean collidable, String name){
        this(id, collidable, name, 0);
    }

    private GameTile(Integer id, Boolean collidable, String name, Integer damage){
        this.id = id;
        this.collidable = collidable;
        this.name = name;
        this.damage = damage;
    }

    public Integer getId() {
        return id;
    }

    public Boolean isCollidable(){
        return collidable;
    }

    public String getName() {
        return name;
    }

    public Integer getDamage() {
        return damage;
    }

    private static HashMap<Integer, GameTile> tileMap;

    static {
        tileMap = new HashMap<>();
        for (GameTile gt:GameTile.values()) {
            GameTile.tileMap.put(gt.getId(), gt);
        }
    }

    public static GameTile getTileById(int id){
        return tileMap.get(id);
    }
}
