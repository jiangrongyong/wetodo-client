package wetodo.handler.task.group;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;
import wetodo.handler.IQBaseHandler;
import wetodo.manager.TaskGroupManager;
import wetodo.model.TaskGroup;
import wetodo.xml.task.group.TaskGroupModifyXmlReader;
import wetodo.xml.task.group.TaskGroupModifyXmlWriter;

import java.sql.Timestamp;

public class IQTaskGroupModifyHandler extends IQBaseHandler {
    protected String namespace = "lacool:todo:modify:group";

    public IQTaskGroupModifyHandler() {
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
        TaskGroup taskGroup = TaskGroupModifyXmlReader.getTaskGroup(lacoolElement);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        taskGroup.setModify_date(now);

        // persistent to db
        TaskGroupManager.getInstance().modify(taskGroup);
        taskGroup = TaskGroupManager.getInstance().find(taskGroup.getTgid());

        // output
        return result(packet, TaskGroupModifyXmlWriter.write(taskGroup.getRoomid(), taskGroup, namespace));
    }
}
