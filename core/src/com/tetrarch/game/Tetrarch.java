package com.tetrarch.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
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
	protected SpriteBatch batch;
	Skin skin;
	protected Stage stage;
	private Viewport viewport;
	private Socket socket;
	private GameMap map;
	public static OrthographicCamera cam;
	public static Player player;
	protected static String pseudo;
	public static String adress;
	public final static int WIDTH = 720;
	public final static int HEIGHT = 480;
	private Touchpad touchpad;

	@Override
	public void create () {
		initSkin();
		batch = new SpriteBatch();
		Gdx.app.log("Debug", "My name: " + pseudo);
		if(Gdx.app.getType().equals(Application.ApplicationType.Android)) {
			Preferences prefs = Gdx.app.getPreferences("My Preferences");
			prefs.putString("player_name", pseudo);
		}
		connectSocket();
		configSocketEvents();
		cam = new OrthographicCamera();
		cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (Gdx.app.getType().equals(Application.ApplicationType.Android)){
			viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), cam);
		}else{
			viewport = new FitViewport(this.WIDTH, this.HEIGHT, cam);
		}
		viewport.apply();

		cam.update();

		stage = new Stage(viewport, batch);
		Gdx.input.setInputProcessor(stage);

		if (Gdx.app.getType().equals(Application.ApplicationType.Android)){
			touchpad = new Touchpad(20, skin);
			touchpad.setBounds(15, 15, 400, 400);
			touchpad.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					float deltaX = ((Touchpad) actor).getKnobPercentX();
					System.out.println("BRUH "+deltaX);
					player.moveXByPrct(deltaX);
				}
			});
			stage.addActor(touchpad);
		}
		map = new TiledGameMap();
	}

	private void initSkin(){
		skin = new Skin(Gdx.files.internal("default/skin/uiskin.json"));
		skin.addRegions(new TextureAtlas(Gdx.files.internal("default/skin/uiskin.atlas")));
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
			if (adress!=null){
				socket = IO.socket("http://"+adress+":8080");
			}else{
				socket = IO.socket("http://localhost:8080");
			}
			socket.connect();
		}catch (Exception e){
			System.out.println("Bruh "+e);
		}
	}

	@Override
	public void render () {
		if (touchpad!=null)
			touchpad.setZIndex(20);
		updateServer(Gdx.graphics.getDeltaTime());
//		System.out.println(touchpad.getKnobPercentX() + " " + touchpad.getKnobPercentY());
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		cam.update();
		map.update(Gdx.graphics.getDeltaTime());
		map.render(cam, batch);

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();


	}

	@Override
	public void dispose () {
		batch.dispose();
		map.dispose();
		stage.dispose();
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

	public static void setPlayerName(String name){
		if (player!=null){
			player.setName(name);
		}
		pseudo=name;
	}

	public static String getPlayerName(){
		if (player!=null){
			return player.getName();
		}else {
			if (pseudo!=null){
				return pseudo;
			}else{
				return "NULL_PLAYER_NAME";
			}
		}
	}

	protected static String getNameFromPrefs(){
		return Gdx.app.getPreferences("My Preferences").getString("player_name");
	}
}
