package wetodo.handler.task.group;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;
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
        taskGroup = TaskGroupManager.getInstance().add(taskGroup);

        // output
        return result(packet, TaskGroupAddXmlWriter.write(taskGroup.getRoomid(), taskGroup, namespace));
    }
}
