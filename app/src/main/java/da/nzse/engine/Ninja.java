package da.nzse.engine;

import android.content.Context;
import android.graphics.BitmapFactory;

import da.nzse.spaceninja.R;

public class Ninja extends Entity {

    Ninja(Context context) {
        super(new Sprite(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ninja), 32, 64, 10, 4
        , true), new BoundingBox2D(0,0,128,64));
        getSprite().setScale(2.0);
        setMaxVelocity(500);
    }

    public void update(double elapsedTime){

    }
}
