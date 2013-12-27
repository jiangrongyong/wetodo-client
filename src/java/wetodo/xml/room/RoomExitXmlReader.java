package wetodo.xml.room;

import org.dom4j.Element;
import org.xmpp.packet.JID;

public class RoomExitXmlReader {

    public static String getRoomId(Element lacoolElement) {
        return lacoolElement.attribute("roomid").getValue();
    }
}
