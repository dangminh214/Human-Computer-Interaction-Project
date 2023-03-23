package da.nzse.engine;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class XMLCommon {
    /// <summary>
    /// advances the XML Parser to the next Start Tag
    /// </summary>
    /// <param name="parser">the xml parser</param>
    public static void XMLNextStart(XmlPullParser parser)
    throws IOException, XmlPullParserException {
        while(parser.getEventType() != XmlPullParser.START_TAG)
            parser.next();
    }

    /// <summary>
    /// Skips a tag in the XML Parser
    /// </summary>
    /// <param name="parser">the XML Parser to read from</param>
    public static void XMLSkip(XmlPullParser parser)
    throws IOException, XmlPullParserException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    /// <summary>
    /// reads the Text from this Tag
    /// </summary>
    /// <param name="parser">the XML Parser to read from</param>
    /// <returns>the read Text</returns>
    public static String XMLReadText(XmlPullParser parser)
    throws IOException, XmlPullParserException, NumberFormatException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /// <summary>
    /// reads an Integer from this Tag
    /// </summary>
    /// <param name="parser">the XML Parser to read from</param>
    /// <returns>the read Integer</returns>
    public static int XMLReadInt(XmlPullParser parser)
    throws IOException, XmlPullParserException, NumberFormatException {
        return Integer.parseInt(XMLReadText(parser));
    }

}
