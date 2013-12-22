package wetodo.handler.task.group;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.muc.ForbiddenException;
import org.jivesoftware.openfire.muc.MUCRole;
import org.jivesoftware.openfire.muc.MUCRoom;
import org.jivesoftware.openfire.muc.MultiUserChatService;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.*;
import wetodo.handler.IQBaseHandler;
import wetodo.manager.TaskGroupManager;
import wetodo.model.TaskGroup;
import wetodo.xml.task.group.TaskGroupAddXmlReader;
import wetodo.xml.task.group.TaskGroupAddXmlWriter;

import java.sql.Timestamp;

public class IQTaskGroupAddHandler extends IQBaseHandler {
    private static final int VERSION_ONE = 1;
    protected String namespace = "lacool:todo:add:group";

    public IQTaskGroupAddHandler() {
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
        TaskGroup taskGroup = TaskGroupAddXmlReader.getTaskGroup(lacoolElement);
        taskGroup.setVersion(VERSION_ONE);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        taskGroup.setCreate_date(now);
        taskGroup.setModify_date(now);

        // persistent to db
        packet.getFrom().toBareJID();
        taskGroup = TaskGroupManager.getInstance().add(taskGroup);

        // Send Muc msg
        JID rJid = new JID(taskGroup.getRoomid());
        MultiUserChatService chatService =
                XMPPServer.getInstance().getMultiUserChatManager().getMultiUserChatService(rJid);
        MUCRoom room = chatService.getChatRoom(rJid.getNode());
        org.xmpp.packet.Message message = new org.xmpp.packet.Message();
        message.setTo(rJid);
        PacketExtension extension = new PacketExtension(packet.getChildElement().createCopy());
        message.addExtension(extension);
        message.setType(Message.Type.groupchat);
        MUCRole senderRole;
        try {
            senderRole = room.getOccupant(packet.getFrom().getNode());
            room.sendPublicMessage(message, senderRole);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        } catch (ForbiddenException e) {
            e.printStackTrace();
        }

        // output
        return result(packet, TaskGroupAddXmlWriter.write(taskGroup.getRoomid(), taskGroup, namespace));
    }
}
