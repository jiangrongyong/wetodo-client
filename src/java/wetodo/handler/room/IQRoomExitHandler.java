package wetodo.handler.room;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.muc.ConflictException;
import org.jivesoftware.openfire.muc.ForbiddenException;
import org.jivesoftware.openfire.muc.NotAllowedException;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;
import wetodo.handler.IQBaseHandler;
import wetodo.manager.RoomManager;
import wetodo.model.Room;
import wetodo.xml.room.RoomAddXmlReader;
import wetodo.xml.room.RoomAddXmlWriter;
import wetodo.xml.room.RoomExitXmlReader;
import wetodo.xml.room.RoomExitXmlWriter;

public class IQRoomExitHandler extends IQBaseHandler {
    protected String namespace = "lacool:muc:exit:room";

    public IQRoomExitHandler() {
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
        String roomJidStr = RoomExitXmlReader.getRoomId(lacoolElement);
        JID userJid = packet.getFrom();

        // persistent to db
        JID roomJid = new JID(roomJidStr);
        try {
            RoomManager.getInstance().exit(roomJid, userJid);
        } catch (NotAllowedException e) {
        } catch (UserNotFoundException e) {
        } catch (ConflictException e) {
        } catch (ForbiddenException e) {
        }

        // output
        return result(packet, RoomExitXmlWriter.write(namespace));
    }
}
