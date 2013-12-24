package wetodo.handler.task;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.muc.ForbiddenException;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;
import wetodo.handler.IQBaseHandler;
import wetodo.manager.TaskManager;
import wetodo.model.Task;
import wetodo.model.TaskGroup;
import wetodo.msg.Msg;
import wetodo.xml.task.TaskAddXmlWriter;
import wetodo.xml.task.TaskDelXmlReader;
import wetodo.xml.task.TaskDelXmlWriter;

import java.util.Map;

public class IQTaskDelHandler extends IQBaseHandler {
    protected String namespace = "lacool:todo:delete:task";

    public IQTaskDelHandler() {
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
        Task task = TaskDelXmlReader.getTask(lacoolElement);

        // persistent to db
        Map resultMap = TaskManager.getInstance().del(task);
        TaskGroup taskGroup = (TaskGroup) resultMap.get("taskgroup");

        // Send Muc msg
        try {
            JID roomJid = new JID(task.getRoomid());
            Element extensionElement = TaskDelXmlWriter.write(task.getRoomid(), task, taskGroup, namespace);
            String nickname = packet.getFrom().getNode();
            Msg.groupchat(roomJid, extensionElement, nickname);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        } catch (ForbiddenException e) {
            e.printStackTrace();
        }

        // output
        return result(packet, TaskDelXmlWriter.write(task.getRoomid(), task, taskGroup, namespace));
    }
}
