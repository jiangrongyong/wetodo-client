package wetodo.handler.room;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;
import wetodo.handler.IQBaseHandler;
import wetodo.manager.RoomManager;
import wetodo.model.Room;
import wetodo.xml.room.RoomAddXmlReader;
import wetodo.xml.room.RoomAddXmlWriter;

public class IQRoomCreateHandler extends IQBaseHandler {
    protected String namespace = "lacool:muc:create:room";

    public IQRoomCreateHandler() {
        super();
        this.info = new IQHandlerInfo("lacool", namespace);
    }

    @Override
    public IQ handleIQ(IQ packet) {
        // valid
        if (!packet.getType().equals(IQ.Type.set)) {
            return systemError(packet, PacketError.Condition.bad_request);
        }

        // xml reader
        Element lacoolElement = packet.getChildElement();
        String subject = RoomAddXmlReader.getSubject(lacoolElement);
        JID userJid = packet.getFrom();

        // generate room jid on server side, not client sid.
        JID roomJid = RoomManager.getInstance().generateRoomJid(userJid);

        // persistent to db
        Room room = RoomManager.getInstance().create(roomJid, userJid, subject);

        // output
        return result(packet, RoomAddXmlWriter.write(room, namespace));
    }
}
