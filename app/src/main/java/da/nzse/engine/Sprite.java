package da.nzse.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import da.nzse.spaceninja.R;

/// <summary>
/// Represents a Sprite.
/// Sprites can be single Pictures or Animations.
/// The Class facilitates for Animations by providing
/// Functions that can offset the displayed Image
/// from the Bitmap by a multiple of the width and
/// height given in the Constructor
/// </summary>
public class Sprite implements Drawable {
    private Bitmap mBitmap;
    private int mDimensions[] = {0,0};
    private int mColumns = 0;
    private int mRows = 0;
    private int mCurrentCol = 0;
    private int mCurrentRow = 0;
    private double mScale = 1.0;
    private static Paint msPaint = null;
    private boolean mDrawWithOffset = false;

    /// <summary>
    /// Creates a Sprite from the given Sprite sheet
    /// </summary>
    /// <param name="spriteSheet">the Bitmap to use as base for the Sprite</param>
    /// <param name="width">the Width of the Sprite in pixels</param>
    /// <param name="height">the Height of the Sprite ion pixels</param>
    /// <param name="cols">the Number of Columns on the Sprite sheet (for animations)</param>
    /// <param name="rows">the Number of Rows on the Sprite sheet (for animation)</param>
    Sprite(Bitmap spriteSheet, int width, int height, int cols, int rows, boolean drawWithOffset){
        mBitmap = spriteSheet;
        mDimensions[0] = width;
        mDimensions[1] = height;
        mColumns = cols;
        mRows = rows;
        mDrawWithOffset = drawWithOffset;

        if(msPaint == null){
            msPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        }
    }

    /// <summary>
    /// Creates a Sprite from the given Sprite sheet
    /// </summary>
    /// <param name="spriteSheet">the Bitmap to use as base for the Sprite</param>
    /// <param name="width">the Width of the Sprite in pixels</param>
    /// <param name="height">the Height of the Sprite ion pixels</param>
    /// <param name="cols">the Number of Columns on the Sprite sheet (for animations)</param>
    /// <param name="rows">the Number of Rows on the Sprite sheet (for animation)</param>
    Sprite(Bitmap spriteSheet, int width, int height, int cols, int rows){
        mBitmap = spriteSheet;
        mDimensions[0] = width;
        mDimensions[1] = height;
        mColumns = cols;
        mRows = rows;

        if(msPaint == null){
            msPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        }
    }

    /// <summary>
    /// Creates a Sprite from the given Sprite sheet.
    /// This version of the Constructor does not create an animation.
    /// </summary>
    /// <param name="spriteSheet">the Bitmap to use as base for the Sprite</param>
    /// <param name="width">the Width of the Sprite in pixels</param>
    /// <param name="height">the Height of the Sprite in pixels</param>
    Sprite(Bitmap spriteSheet, int width, int height){
        this(spriteSheet, width, height, 1, 1);
    }

    /// <summary>
    /// Sets the Animation State to the given Row and Column
    /// </summary>
    /// <param name="col">the Column in the Sprite sheet with the Animation State</param>
    /// <param name="row">the Row in the Sprite sheet with the Animation State</param>
    /// <returns>True when successful</returns>
    public boolean setState(int col, int row){
        if(col < mColumns && row < mRows){
            mCurrentCol = col;
            mCurrentRow = row;
            return true;
        }
        return false;
    }

    /// <summary>
    /// sets the current Column to the given column without modifying the row
    /// </summary>
    /// <param name="column">the Column to set the Sprites State to</param>
    /// <returns>true on success false when nothing changed</returns>
    public boolean setColumn(int column){
        return setState(column, mCurrentRow);
    }

    /// <summary>
    /// sets the current Row to the given column without modifying the Column
    /// </summary>
    /// <param name="row">the Row to set the Sprites State to</param>
    /// <returns>true on success false when nothing changed</returns>
    public boolean setRow(int row){
        return setState(mCurrentCol, row);
    }

    /// <summary>
    /// Returns the currently displayed Column from the Sprite sheet
    /// </summary>
    /// <returns>The number of the Column</returns>
    public int getColumn() {
        return mCurrentCol;
    }

    /// <summary>
    /// Returns the currently displayed Row from the Sprite sheet
    /// </summary>
    /// <returns>The number of the Row</returns>
    public int getRow(){
        return mCurrentRow;
    }

    /// <summary>
    /// Returns the Width of the Sprite
    /// </summary>
    /// <returns>The width of the Sprite</returns>
    public int getWidth(){
        return mDimensions[0];
    }

    /// <summary>
    /// Returns the Height of the Sprite
    /// </summary>
    /// <returns>The Height of the Sprite</returns>
    public int getHeight(){
        return mDimensions[1];
    }

    /// <summary>
    /// Cycles the Animation State to the next Row in the Sprite sheet.
    /// When there are no more Rows, restart at the beginning.
    /// </summary>
    public void cycleNextRow(){
        mCurrentRow = (mCurrentRow+1)%mRows;
    }

    /// <summary>
    /// Cycles the Animation state to the previous Row in the Sprite sheet.
    /// When the Row is already 0, wrap around to the maximum Row.
    /// </summary>
    public void cyclePreviousRow(){
        mCurrentRow = mCurrentRow-1<0?mRows-1:mCurrentRow-1;
    }

    /// <summary>
    /// Cycle the Animation State to the next Column in the Sprite sheet.
    /// When there are no more Columns, restart at the beginning.
    /// </summary>
    public void cycleNextColumn(){ mCurrentCol = (mCurrentCol+1)%mColumns; }

    /// <summary>
    /// Cycle the Animation State to the previous Column in the Sprite sheet.
    /// When the Column is already 0, wrap around to the maximum Column
    /// </summary>
    public void cyclePreviousColumn(){
        mCurrentCol = mCurrentCol-1<0?mColumns-1:mCurrentCol-1;
    }

    /// <summary>
    /// set the Scale of the Bitmap
    /// </summary>
    /// <param name="scale">the new Scale of the Bitmap</param>
    public void setScale(double scale){ mScale = scale;}

    @Override
    public void draw(Canvas canvas, float x, float y, Matrix transform) {

        float xOffset = (float)(((double)mDimensions[0]/2.0)*mScale);
        float yOffset = (float)(((double)mDimensions[1]/2.0)*mScale);
        if(!mDrawWithOffset){
            xOffset = (float)(((double)mDimensions[0])*mScale);
            yOffset = (float)(((double)mDimensions[1])*mScale);
        }

        Rect source = new Rect(
                mDimensions[0] * mCurrentCol,
                mDimensions[1] * mCurrentRow,
                (mDimensions[0] * mCurrentCol) + mDimensions[0],
                (mDimensions[1] * mCurrentRow) + mDimensions[1]
        );

        Rect destination;
        if(mDrawWithOffset){
            destination = new Rect(
                    (int) (x - xOffset),
                    (int) (y - yOffset),
                    (int) (x + xOffset),
                    (int) (y + yOffset)
            );
        } else {
            destination = new Rect(
                    (int) (x),
                    (int) (y),
                    (int) (x + xOffset),
                    (int) (y + yOffset)
            );
        }
        canvas.drawBitmap(mBitmap, source, destination, msPaint);
/*
        Log.d("Sprite","DrawStats:" +
                "Dimensions ["+mDimensions[0] + ","+mDimensions[1]+"]\n" +
                "Offset: "+new dVec2D(xOffset,yOffset)+"\n" +
                "Scale: " +mScale+"\n" +
                "Position: "+new dVec2D(x,y)+"\n" +
                "actual WH: "+ new dVec2D(
                        (int) (x + (mDimensions[0] * mScale) + xOffset),
                        (int) (y + (mDimensions[1] * mScale) + yOffset)).getDifferende(
                new dVec2D(
                        (int) (x - xOffset),
                        (int) (y - yOffset))));
 */
    }

    /// <summary>
    /// reads a Sprite from a Tag in na XML file
    /// <summary>
    /// <param name="parser">the XML Parser to read from</param>
    /// <param name="context">the Context to read in Resources</param>
    /// <returns>a Sprite read from the XML File</returns>
    public static Sprite readSprite(XmlPullParser parser, Context context)
    throws IOException, XmlPullParserException {
        Sprite sprite;

        int width=0, height=0, cols=1, rows=1, curCol=0, curRow=0;
        Bitmap bitmap = null;

        parser.require(XmlPullParser.START_TAG, null, "Sprite");

        String widthString = parser.getAttributeValue(null, "width");
        String heightString = parser.getAttributeValue(null, "height");
        String columnsString = parser.getAttributeValue(null, "columns");
        String rowsString = parser.getAttributeValue(null, "rows");
        String curColString = parser.getAttributeValue(null, "displayColumn");
        String curRowString = parser.getAttributeValue(null, "displayRow");

        String resourceLocation = parser.getAttributeValue(null,"src");

        if(widthString!=null) width = Integer.parseInt(widthString);
        if(heightString!=null) height = Integer.parseInt(heightString);
        if(columnsString!=null) cols = Integer.parseInt(columnsString);
        if(rowsString!=null) rows = Integer.parseInt(rowsString);
        if(curColString!=null) curCol = Integer.parseInt(curColString);
        if(curRowString!=null) curRow = Integer.parseInt(curRowString);

        bitmap = BitmapFactory.decodeResource(
                context.getResources(),
                Integer.parseInt(resourceLocation.substring(1))
        );

        sprite = new Sprite(bitmap, width, height, cols, rows);
        sprite.setState(curCol,curRow);

        parser.next();
        parser.require(XmlPullParser.END_TAG, null, "Sprite");
        return sprite;
    }
}
