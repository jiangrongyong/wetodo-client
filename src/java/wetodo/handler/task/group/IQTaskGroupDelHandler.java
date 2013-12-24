package wetodo.handler.task.group;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.muc.ForbiddenException;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;
import wetodo.handler.IQBaseHandler;
import wetodo.manager.TaskGroupManager;
import wetodo.model.TaskGroup;
import wetodo.msg.Msg;
import wetodo.xml.task.group.TaskGroupAddXmlWriter;
import wetodo.xml.task.group.TaskGroupDelXmlReader;
import wetodo.xml.task.group.TaskGroupDelXmlWriter;

public class IQTaskGroupDelHandler extends IQBaseHandler {
    protected String namespace = "lacool:todo:delete:group";

    public IQTaskGroupDelHandler() {
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
        TaskGroup taskGroup = TaskGroupDelXmlReader.getTaskGroup(lacoolElement);

        // persistent to db
        TaskGroupManager.getInstance().del(taskGroup.getTgid());

        // Send Muc msg
        try {
            JID roomJid = new JID(taskGroup.getRoomid());
            Element extensionElement = TaskGroupDelXmlWriter.write(taskGroup.getRoomid(), taskGroup, namespace);
            String nickname = packet.getFrom().getNode();
            Msg.groupchat(roomJid, extensionElement, nickname);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        } catch (ForbiddenException e) {
            e.printStackTrace();
        }

        // output
        return result(packet, TaskGroupDelXmlWriter.write(taskGroup.getRoomid(), taskGroup, namespace));
    }
}
