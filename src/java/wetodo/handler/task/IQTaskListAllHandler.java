package wetodo.handler.task;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;
import wetodo.handler.IQBaseHandler;
import wetodo.manager.TaskManager;
import wetodo.model.Task;
import wetodo.xml.task.TaskListAllXmlWriter;
import wetodo.xml.task.TaskListXmlReader;

import java.util.List;

public class IQTaskListAllHandler extends IQBaseHandler {
    protected String namespace = "lacool:todo:fetch:all";

    public IQTaskListAllHandler() {
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

        // persistent to db
        List<Task> taskList = TaskManager.getInstance().list_all(roomid);

        // output
        return result(packet, TaskListAllXmlWriter.write(roomid, taskList, namespace));
    }
}
