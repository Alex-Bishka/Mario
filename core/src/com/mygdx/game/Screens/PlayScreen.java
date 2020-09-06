package com.mygdx.game.Screens;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.Sprites.Goomba;
import com.mygdx.game.Sprites.Mario;
import com.mygdx.game.Tools.B2WorldCreator;
import com.mygdx.game.Tools.WorldContactListener;


public class PlayScreen implements Screen {
    //Gdx.app.setLogLevel(Application.LOG_DEBUG);

    private MyGdxGame game;
    private TextureAtlas atlas;

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    // Tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    // Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr; // gives graphical representation of fixtures and bodies within box2d world

    // sprites
    private Mario player;
    private Goomba goomba;

    private Music music;

    public PlayScreen(MyGdxGame game) {
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        this.game = game;

        // create cam used to follow mario through cam world
        gameCam = new OrthographicCamera();

        // create a FitViewport to maintain virtual aspect ratio
        gamePort = new FitViewport(MyGdxGame.V_WIDTH / MyGdxGame.PPM, MyGdxGame.V_HEIGHT / MyGdxGame.PPM, gameCam);

        // create our game HUD for scores/timers/level info
        hud = new Hud(game.batch);

        // loading in the map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MyGdxGame.PPM);

        // centering the game cam - casting float onto ints due to function input requirements
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        // create our Box2D world, setting no gravity in x, -10 in Y and allow bodies to sleep
        world = new World(new Vector2(0, -10), true);
        //allows for debug lines of our box2D world
        b2dr = new Box2DDebugRenderer();

        new B2WorldCreator(this);

        // create mario in our game world
        player = new Mario(this);

        world.setContactListener(new WorldContactListener());

        music = MyGdxGame.manager.get("Audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.play();

        goomba = new Goomba(this, .32f, .32f);
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }

    // TODO: fix key mapping and isKeyJustPressed
    // takes in float delta time
    public void handleInput(float dt) {
        // down is mapped to my right arrow key
        // up is mapped to my left arrow key
        // left is mapped to my up arrow key
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            Gdx.app.log("Debug", "W is pressed");
            player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && player.b2body.getLinearVelocity().x <= 2) {
            Gdx.app.log("Debug", "R is pressed");
            player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && player.b2body.getLinearVelocity().x >= -2) {
            Gdx.app.log("Debug", "L is pressed");
            player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }
    }

    // takes in float delta-time
    // updating of game world - check to see if any inputs happen (key presses etc)
    public void update (float dt) {
        handleInput(dt);

        world.step(1/60f, 6, 2);

        player.update(dt);
        goomba.update(dt);
        hud.update(dt);

        gameCam.position.x = player.b2body.getPosition().x;

        // update camera as it moves - every iteration of render cycle
        gameCam.update();

        // what am I updating? Look below - gamCam
        renderer.setView(gameCam);
    }

    @Override
    public void render(float delta) {
        // separate our update logic from render
        update(delta);

        // Clear the game screen with Black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render our game map
        renderer.render();

        // render our Box2DDebugLines
        b2dr.render(world, gameCam.combined);


        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        goomba.draw(game.batch);
        game.batch.end();

        // Set our batch to now draw what the Hud camera sees
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);  // what will be shown via camera
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
