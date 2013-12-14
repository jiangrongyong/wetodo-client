package wetodo.handler.pay;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;
import wetodo.dao.UserDAO;
import wetodo.error.IQError;
import wetodo.exception.IapIdNotExistsException;
import wetodo.exception.ReceiptAlreadyExistsException;
import wetodo.exception.ReceiptIAPValidFailException;
import wetodo.handler.IQBaseHandler;
import wetodo.manager.PayManager;
import wetodo.model.User;
import wetodo.xml.pay.PayPurchaseXmlReader;
import wetodo.xml.pay.PayPurchaseXmlWriter;

public class IQPayPurchaseHandler extends IQBaseHandler {
    protected String namespace = "lacool:member:verify:product";

    public IQPayPurchaseHandler() {
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
        String iapId = PayPurchaseXmlReader.getIapId(lacoolElement);
        String receipt = PayPurchaseXmlReader.getReceipt(lacoolElement);
        String username = packet.getFrom().getNode();

        // persistent to db
        try {
            PayManager.getInstance().purchase(username, receipt, iapId);
        } catch (ReceiptAlreadyExistsException e) {
            return error(packet, IQError.Condition.receipt_exist);
        } catch (ReceiptIAPValidFailException e) {
            return error(packet, IQError.Condition.receipt_iap_valid_fail);
        } catch (IapIdNotExistsException e) {
            return error(packet, IQError.Condition.iapid_not_exist);
        }
        User user = UserDAO.findByUsername(username);

        // output
        return result(packet, PayPurchaseXmlWriter.write(namespace, user));
    }
}
