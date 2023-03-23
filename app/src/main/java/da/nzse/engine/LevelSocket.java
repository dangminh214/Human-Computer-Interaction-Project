package da.nzse.engine;

import static da.nzse.engine.XMLCommon.XMLReadText;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

enum LevelSocketSide { Left, Right };

public class LevelSocket {

    private String mSocketData;
    public LevelSocketSide mSide = LevelSocketSide.Left;

    /// <summary>
    /// Checks weather another Socket matches this one
    /// </summary>
    /// <param name="other">the LevelSocket to check against</param>
    public boolean matches(LevelSocket other){
        return mSocketData.equals(other.mSocketData);
    }

    /// <summary>
    /// Creates a LevelSocket based on a String readable from a Line in a File or something...
    /// </summary>
    /// <param name="stringRepresentation">the StringRepresentation for the Socket</param>
    LevelSocket(String stringRepresentation) {
        mSocketData = stringRepresentation;
    }

    /// <summary>
    /// Reads a LevelSocket from a XML File
    /// </summary>
    /// <param name="parser">the XML Parser</param>
    /// <returns>a new LevelSocket</returns>
    public static LevelSocket XMLReadLevelSocket(XmlPullParser parser)
    throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "LevelSocket");

        LevelSocketSide side;
        switch(parser.getAttributeValue(null, "side")){
            default:
            case "left":
            case "Left":
                side = LevelSocketSide.Left;
                break;
            case "right":
            case "Right":
                side = LevelSocketSide.Right;
                break;
        }

        LevelSocket socket = new LevelSocket(XMLReadText(parser));
        socket.mSide = side;
        parser.require(XmlPullParser.END_TAG, null, "LevelSocket");
        return socket;
    };

}
