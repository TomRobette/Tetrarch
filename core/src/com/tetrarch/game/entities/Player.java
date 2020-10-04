package com.tetrarch.game.entities;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tetrarch.game.Tetrarch;
import com.tetrarch.game.world.GameMap;

public class Player extends Entity {

    private final static int MAX_SPEED = 360;
    private int sens = 0;
    private float speed = 0;
    private final static int JUMP_VELOCITY = 5;
    private String name;
    public float multZoom = 3;
    boolean keyPressed;

    Sprite image;

    public Player(String id, float x, float y, EntityType type, GameMap map){
        super.create(id, x, y, type, map);

        image = new Sprite();
        try {
            this.image.setRegion(new Texture("entities/player.png"));
        }catch (Exception e){
            Gdx.app.postRunnable(new Runnable(){
                @Override
                public void run() {
                    image.setRegion(new Texture("entities/player.png"));
                }
            });
        }
    }

    public Player(String id, float x, float y, EntityType type, GameMap map, String name){
        super.create(id, x, y, type, map);
        this.name = name;
        image = new Sprite();
        try {
            this.image.setRegion(new Texture("entities/player.png"));
        }catch (Exception e){
            Gdx.app.postRunnable(new Runnable(){
                @Override
                public void run() {
                    image.setRegion(new Texture("entities/player.png"));
                }
            });
        }
    }

    @Override
    public void create (String id, float x, float y, EntityType type, GameMap map){
        image = new Sprite();
        image.setRegion(new Texture("entities/player.png"));
    }

    @Override
    public void update(float deltaTime, float gravity) {
        boolean jump = false;
        if (id.equals(Tetrarch.player.id)){
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.UP)){
                jump = true;
            }
            if (jump && grounded){
                this.velocityY += JUMP_VELOCITY * getWeight();
            }else if(jump && !grounded && velocityY > 0){
                if (Gdx.input.isKeyPressed(Input.Keys.C)){
                    this.velocityY += JUMP_VELOCITY * getWeight();
                }else{
                    this.velocityY += JUMP_VELOCITY * getWeight() * deltaTime;
                }
            }
        }

        super.update(deltaTime, gravity);

        if (id.equals(Tetrarch.player.id)){
            keyPressed = false;
            if (Gdx.input.isKeyJustPressed(Input.Keys.O)){
                multZoom-=1;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.L)){
                multZoom+=1;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)){
                keyPressed = true;
                sens=-1;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)){
                keyPressed = true;
                sens=1;
            }

            if (keyPressed){
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)){
                    if (sens==-1){
                        acceleration();
                    }else{
                        decelerationOpposite();
                    }
                }else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)){
                    if (sens==1){
                        acceleration();
                    }else{
                        decelerationOpposite();
                    }
                }

            }else{
                if (speed!=0){
                    speed*=0.976;
                }else{
                    sens=0;
                }
            }
            moveX((int) (speed * sens * deltaTime));
        }
    }

    public void moveXByPrct(float sensF){
        if (id.equals(Tetrarch.player.id)){
            int direction = 1;
            if (sensF<0){
                direction=-1;
            }
            sens = direction;
            acceleration();
        }
    }

    public String getName() {
        if (name!=null)
            return name;
        return "NULL_PLAYER_NAME";
    }

    public void setName(String name) {
        if (name!=null && name!="")
            this.name = name;
    }

    private void acceleration(){
        double velocity = 1.3;
        if (speed* velocity > MAX_SPEED){
            speed = MAX_SPEED;
        }else{
            if (speed == 0) {
                speed=10;
            }
            speed*= velocity;
        }
    }

    private void decelerationOpposite(){
        if (speed<=1 && speed >0){
            speed=0;
            sens=0;
        }else if (speed>1){
            speed*=0.6;
        }
    }


    @Override
    public void render(SpriteBatch batch) {
        if (image!=null) {
            batch.draw(image, pos.x, pos.y, getWidth(), getHeight());
            if (id.equals(Tetrarch.player.id)) {
                Tetrarch.cam.position.set(pos.x, pos.y, 0);
                if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
                    Tetrarch.cam.viewportWidth = Gdx.graphics.getWidth() / multZoom;
                    Tetrarch.cam.viewportHeight = Gdx.graphics.getHeight() / multZoom;
                }
            }
        }
    }
}
