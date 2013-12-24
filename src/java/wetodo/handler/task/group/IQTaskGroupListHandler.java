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
import wetodo.xml.task.group.TaskGroupListXmlReader;
import wetodo.xml.task.group.TaskGroupListXmlWriter;

import java.util.List;

public class IQTaskGroupListHandler extends IQBaseHandler {
    protected String namespace = "lacool:todo:fetch:group_list";

    public IQTaskGroupListHandler() {
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
        String roomid = TaskGroupListXmlReader.getRoomid(lacoolElement);

        // persistent to db
        List<TaskGroup> list = TaskGroupManager.getInstance().list(roomid);

        // Send Muc msg
        try {
            JID roomJid = new JID(roomid);
            Element extensionElement = TaskGroupListXmlWriter.write(roomid, list, namespace);
            String nickname = packet.getFrom().getNode();
            Msg.groupchat(roomJid, extensionElement, nickname);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        } catch (ForbiddenException e) {
            e.printStackTrace();
        }

        // output
        return result(packet, TaskGroupListXmlWriter.write(roomid, list, namespace));
    }
}
