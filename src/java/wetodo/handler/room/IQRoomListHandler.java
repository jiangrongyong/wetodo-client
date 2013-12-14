package wetodo.handler.room;

import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.session.ClientSession;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;
import wetodo.handler.IQBaseHandler;
import wetodo.manager.RoomManager;
import wetodo.model.Room;
import wetodo.xml.room.RoomListXmlWriter;

import java.util.List;

public class IQRoomListHandler extends IQBaseHandler {
    protected String namespace = "lacool:muc:fetch:room_list";

    public IQRoomListHandler() {
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

        // persistent to db
        List<Room> list = RoomManager.getInstance().list(jid);

        // output
        return result(packet, RoomListXmlWriter.write(list, namespace));
    }
}
