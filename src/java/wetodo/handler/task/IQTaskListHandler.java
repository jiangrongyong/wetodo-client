package wetodo.handler.task;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;
import wetodo.handler.IQBaseHandler;
import wetodo.manager.TaskGroupManager;
import wetodo.manager.TaskManager;
import wetodo.model.Task;
import wetodo.model.TaskGroup;
import wetodo.xml.task.TaskListXmlReader;
import wetodo.xml.task.TaskListXmlWriter;

import java.util.List;

public class IQTaskListHandler extends IQBaseHandler {
    protected String namespace = "lacool:todo:fetch:task_list";

    public IQTaskListHandler() {
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
        String roomid = TaskListXmlReader.getRoomid(lacoolElement);
        String tgid = TaskListXmlReader.getTgid(lacoolElement);

        // persistent to db
        List<Task> taskList = TaskManager.getInstance().list(roomid, tgid);
        TaskGroup taskGroup = TaskGroupManager.getInstance().find(tgid);

        // output
        return result(packet, TaskListXmlWriter.write(roomid, taskList, taskGroup, namespace));
    }
}
