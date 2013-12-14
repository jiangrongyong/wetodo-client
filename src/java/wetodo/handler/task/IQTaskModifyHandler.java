package wetodo.handler.task;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;
import wetodo.handler.IQBaseHandler;
import wetodo.manager.TaskManager;
import wetodo.model.Task;
import wetodo.model.TaskGroup;
import wetodo.xml.task.TaskModifyXmlReader;
import wetodo.xml.task.TaskModifyXmlWriter;

import java.sql.Timestamp;
import java.util.Map;

public class IQTaskModifyHandler extends IQBaseHandler {
    protected String namespace = "lacool:todo:modify:task";

    public IQTaskModifyHandler() {
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
        Task task = TaskModifyXmlReader.getTask(lacoolElement);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        task.setModify_date(now);

        // persistent to db
        Map resultMap = TaskManager.getInstance().modify(task);
        task = (Task) resultMap.get("task");
        TaskGroup taskGroup = (TaskGroup) resultMap.get("taskgroup");

        // output
        return result(packet, TaskModifyXmlWriter.write(task.getRoomid(), task, taskGroup, namespace));
    }
}
