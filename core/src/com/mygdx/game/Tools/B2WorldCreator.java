package com.mygdx.game.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Sprites.Brick;
import com.mygdx.game.Sprites.Coin;

public class B2WorldCreator {
    public B2WorldCreator(PlayScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        // creating bodies/fixtures to game world
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // .get(index) in the for-loop is the index of the objects in the .tmx file
        // create ground bodies/fixtures
        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            // getting rectangle object itself
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            // defining body - static in this case
            // (static - no movement, Dynamic - moves, Kinematic - can be moved but defy gravity, i.e. platform)
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MyGdxGame.PPM, (rect.getY() + rect.getHeight() / 2)/ MyGdxGame.PPM);

            // adding body to 2d world
            body = world.createBody(bdef);

            // defining shape of fixture
            shape.setAsBox((rect.getWidth() / 2) / MyGdxGame.PPM, (rect.getHeight() / 2)/ MyGdxGame.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);  // adds fixture to the body
        }

        // create pipe bodies/fixtures
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            // getting rectangle object itself
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            // defining body - static in this case
            // (static - no movement, Dynamic - moves, Kinematic - can be moved but defy gravity, i.e. platform)
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MyGdxGame.PPM, (rect.getY() + rect.getHeight() / 2) / MyGdxGame.PPM);

            // adding body to 2d world
            body = world.createBody(bdef);

            // defining shape of fixture
            shape.setAsBox((rect.getWidth() / 2) / MyGdxGame.PPM, (rect.getHeight() / 2) / MyGdxGame.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MyGdxGame.OBJECT_BIT;
            body.createFixture(fdef);  // adds fixture to the body
        }

        // create brick bodies/fixtures
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            // getting rectangle object itself
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            new Brick(screen, rect);
        }

        // create coin bodies/fixtures
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            // getting rectangle object itself
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            new Coin(screen, rect);
        }
    }

}
