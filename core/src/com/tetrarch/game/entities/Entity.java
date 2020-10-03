package com.tetrarch.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.tetrarch.game.world.GameMap;

public abstract class Entity {

    public String id;
    protected Vector2 pos;
    protected EntityType type;
    protected float velocityY = 0;
    protected GameMap map;
    protected Boolean grounded = false;
    private Vector2 oldPos;

    public void create(String id, float x, float y, EntityType type, GameMap map){
        this.id=id;
        this.pos = new Vector2(x, y);
        this.oldPos = this.pos;
        this.type = type;
        this.map = map;
    }

    protected void moveX(int amount){
        float newX = pos.x + amount;
        if (!map.doesRectCollideWithMap(newX, pos.y, getWidth(), getHeight())){
            this.oldPos.x = this.pos.x;
            this.pos.x = newX;
        }
    }

    public void update(float deltaTime, float gravity){
        float newY = pos.y;

        this.velocityY += gravity * deltaTime * getWeight();
        newY += this.velocityY * deltaTime;

        if (map.doesRectCollideWithMap(pos.x, newY, getWidth(), getHeight())){
            if (velocityY < 0){
                this.pos.y = (float) Math.floor(pos.y);
                grounded = true;
            }
            this.velocityY = 0;
        }else{
            this.oldPos.y = this.pos.y;
            this.pos.y = newY;
            grounded = false;
        }
    }

    public abstract void render(SpriteBatch batch);

    public Vector2 getPos() {
        return pos;
    }

    public float getX(){
        return pos.x;
    }

    public float getY() {
        return pos.y;
    }

    public void setPos(double x, double y){
        this.pos.x = (float) x;
        this.pos.y = (float) y;
    }

    public EntityType getType() {
        return type;
    }

    public Boolean getGrounded() {
        return grounded;
    }

    public float getWidth(){
        return type.getWidth();
    }

    public float getHeight(){
        return type.getHeight();
    }

    public float getWeight(){
        return type.getWeight();
    }

    public boolean hasMoved(){
        if (this.oldPos.x != this.pos.x || this.oldPos.y != this.pos.y)
            return true;
        return false;
    }


}
