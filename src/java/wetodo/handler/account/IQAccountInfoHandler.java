package wetodo.handler.account;

import org.jivesoftware.openfire.IQHandlerInfo;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;
import wetodo.dao.UserDAO;
import wetodo.handler.IQBaseHandler;
import wetodo.model.User;
import wetodo.xml.account.AccountInfoXmlWriter;

public class IQAccountInfoHandler extends IQBaseHandler {
    protected String namespace = "lacool:member:query:deadline";

    public IQAccountInfoHandler() {
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
        JID userJid = packet.getFrom();
        String username = userJid.getNode();

        // persistent to db
        User user = UserDAO.findByUsername(username);

        // output
        return result(packet, AccountInfoXmlWriter.write(namespace, user));
    }
}
