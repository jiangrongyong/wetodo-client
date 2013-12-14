package wetodo.handler.room;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;
import wetodo.handler.IQBaseHandler;
import wetodo.manager.RoomManager;
import wetodo.model.User;
import wetodo.xml.room.RoomMemberXmlReader;
import wetodo.xml.room.RoomMemberXmlWriter;

import java.util.List;

public class IQRoomMemberHandler extends IQBaseHandler {
    protected String namespace = "lacool:muc:fetch:friend";

    public IQRoomMemberHandler() {
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
        Element lacoolElement = packet.getChildElement();
        String roomId = RoomMemberXmlReader.getRoomId(lacoolElement);

        // persistent to db
        List<User> list = RoomManager.getInstance().fetchMemberList(roomId);

        // output
        return result(packet, RoomMemberXmlWriter.write(list, namespace));
    }
}
