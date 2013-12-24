package wetodo.msg;

import org.dom4j.Element;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.muc.ForbiddenException;
import org.jivesoftware.openfire.muc.MUCRole;
import org.jivesoftware.openfire.muc.MUCRoom;
import org.jivesoftware.openfire.muc.MultiUserChatService;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.PacketExtension;

public class Msg {

    public static void groupchat(JID roomJid, Element extensionElement, String nickname) throws UserNotFoundException, ForbiddenException {
        MultiUserChatService chatService =
                XMPPServer.getInstance().getMultiUserChatManager().getMultiUserChatService(roomJid);
        MUCRoom room = chatService.getChatRoom(roomJid.getNode());

        Message message = new Message();
        message.setTo(roomJid);
        PacketExtension extension = new PacketExtension(extensionElement);
        message.addExtension(extension);
        message.setType(Message.Type.groupchat);

        MUCRole senderRole = room.getOccupant(nickname);
        room.sendPublicMessage(message, senderRole);
    }

    public void chat() {

    }
}
