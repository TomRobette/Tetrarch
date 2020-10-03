package com.tetrarch.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tetrarch.game.entities.EntityType;
import com.tetrarch.game.entities.Player;
import com.tetrarch.game.world.GameMap;
import com.tetrarch.game.world.TiledGameMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class Tetrarch extends ApplicationAdapter {
	private final float UPDATE_TIME= 1/30f;
	float timer;
	SpriteBatch batch;
	private Socket socket;
	GameMap map;
	public static OrthographicCamera cam;
	public static Player player;

	@Override
	public void create () {
		batch = new SpriteBatch();
		map = new TiledGameMap();
//		map.addEntity(new Player("a", 800, 50, EntityType.PLAYER, map));
		connectSocket();
		configSocketEvents();
		cam = new OrthographicCamera();
		cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.update();
	}

	public void configSocketEvents(){
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Gdx.app.log("SocketIO", "Connected");
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						player = new Player("SELF", 800, 50, EntityType.PLAYER, map);
						map.addEntity("SELF", player);
					}
				});
			}
		}).on("socketID", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					if (player==null){
						player = new Player("SELF", 800, 50, EntityType.PLAYER, map);
					}
					String bruh = data.getString("id");
					player.id = bruh;
					Gdx.app.log("SocketIO", "My ID: " + player.id);
				} catch (JSONException e) {
					Gdx.app.log("SocketIO", "Error getting ID");
				}
			}
		}).on("newPlayer", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String id = data.getString("id");
					Gdx.app.log("SocketIO", "New player connected with id : "+id);
					map.addEntity(id, new Player(id, 800, 50, EntityType.PLAYER, map));
				}catch (JSONException e){
					Gdx.app.log("SocketIO", "Error getting new player id");
				}
			}
		}).on("playerDisconnected", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String id = data.getString("id");
					map.removeEntity(id);
				}catch (JSONException e){
					Gdx.app.log("SocketIO", "Error getting new player id");
				}
			}
		}).on("getPlayers", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				final JSONArray objects = (JSONArray) args[0];
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						try {
							for (int a=0; a<objects.length(); a++){
								map.addEntity(objects.getJSONObject(a).getString("id"), new Player(objects.getJSONObject(a).getString("id"), objects.getJSONObject(a).getInt("x"), objects.getJSONObject(a).getInt("y"), EntityType.PLAYER, map));
							}
						}catch (JSONException e){
							Gdx.app.log("SocketIO", "Error getting players");
						}
					}
				});
			}
		}).on("playerMoved", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String id = data.getString("id");
					Double x = data.getDouble("x");
					Double y = data.getDouble("y");
					if (map.getEntity(id) != null)
						map.getEntity(id).setPos(x, y);
				}catch (JSONException e){

				}
			}
		});
	}

	private void connectSocket() {
		try {
			socket = IO.socket("http://localhost:8080");
			socket.connect();
		}catch (Exception e){
			System.out.println(e);
		}
	}

	@Override
	public void render () {
		updateServer(Gdx.graphics.getDeltaTime());
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		cam.update();
		map.update(Gdx.graphics.getDeltaTime());
		map.render(cam, batch);
	}

	@Override
	public void dispose () {
		batch.dispose();
		map.dispose();
	}

	public void updateServer(float dt){
		timer += dt;
		if (timer>= UPDATE_TIME && player!=null){

			JSONObject data = new JSONObject();
			try {
				data.put("x", player.getX());
				data.put("y", player.getY());
				socket.emit("playerMoved", data);
			}catch (JSONException e){
				Gdx.app.log("SOCKET.IO", "Error sending update data");
			}
		}
	}
}
