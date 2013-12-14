package wetodo.handler.room;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.muc.CannotBeInvitedException;
import org.jivesoftware.openfire.muc.ConflictException;
import org.jivesoftware.openfire.muc.ForbiddenException;
import org.jivesoftware.openfire.muc.NotAllowedException;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;
import wetodo.dao.UserDAO;
import wetodo.error.IQError;
import wetodo.handler.IQBaseHandler;
import wetodo.manager.RoomManager;
import wetodo.model.User;
import wetodo.xml.room.RoomInviteXmlReader;
import wetodo.xml.room.RoomInviteXmlWriter;

public class IQRoomInviteHandler extends IQBaseHandler {
    protected String namespace = "lacool:muc:invite:friend";

    public IQRoomInviteHandler() {
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
        String roomID = RoomInviteXmlReader.getRoomId(lacoolElement);
        JID inviteeJid = RoomInviteXmlReader.getInviteeJid(lacoolElement);
        JID roomJid = new JID(roomID);
        JID inviterJid = packet.getFrom();
        String inviteeUsername = inviteeJid.getNode();

        // persistent to db
        try {
            RoomManager.getInstance().invite(roomJid, inviterJid, inviteeJid);
        } catch (UserNotFoundException e) {
            return error(packet, IQError.Condition.username_not_exist);
        } catch (NotAllowedException e) {
            return error(packet, IQError.Condition.room_member_overload);
        } catch (ConflictException e) {
            return systemError(packet, PacketError.Condition.internal_server_error);
        } catch (ForbiddenException e) {
            return systemError(packet, PacketError.Condition.internal_server_error);
        } catch (CannotBeInvitedException e) {
            return systemError(packet, PacketError.Condition.internal_server_error);
        }
        User inviteeUser = UserDAO.findByUsername(inviteeUsername);

        // output
        return result(packet, RoomInviteXmlWriter.write(namespace, inviteeUser));
    }
}
