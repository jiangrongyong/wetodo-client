package wetodo.handler.pay.product;

import org.jivesoftware.openfire.IQHandlerInfo;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;
import wetodo.handler.IQBaseHandler;
import wetodo.manager.ProductManager;
import wetodo.model.Product;
import wetodo.xml.pay.product.ProductListXmlWriter;

import java.util.List;

public class IQProductListHandler extends IQBaseHandler {
    protected String namespace = "lacool:member:query:product";

    public IQProductListHandler() {
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

        // persistent to db
        List<Product> list = ProductManager.getInstance().list();

        // output
        return result(packet, ProductListXmlWriter.write(list, namespace));
    }
}
