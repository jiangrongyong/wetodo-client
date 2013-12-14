package wetodo.handler.room;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.session.ClientSession;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;
import wetodo.handler.IQBaseHandler;
import wetodo.manager.RoomManager;
import wetodo.model.Room;
import wetodo.xml.room.RoomQueryXmlReader;
import wetodo.xml.room.RoomQueryXmlWriter;

public class IQRoomQueryHandler extends IQBaseHandler {
    protected String namespace = "lacool:muc:query:room";

    public IQRoomQueryHandler() {
        super();
        this.info = new IQHandlerInfo("lacool", namespace);
    }

    @Override
    public IQ handleIQ(IQ packet) {
        // valid
        if (!packet.getType().equals(IQ.Type.get)) {
            return systemError(packet, PacketError.Condition.bad_request);
        }

        // xml reader
        ClientSession session = sessionManager.getSession(packet.getFrom());
        String jid = session.getAddress().toBareJID();
        Element lacoolElement = packet.getChildElement();
        String roomId = RoomQueryXmlReader.getRoomId(lacoolElement);

        // persistent to db
        Room room = RoomManager.getInstance().query(roomId);

        // output
        return result(packet, RoomQueryXmlWriter.write(room, namespace));
    }
}
