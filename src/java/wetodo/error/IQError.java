package wetodo.error;

import org.dom4j.Element;

public class IQError {

    public static Element getError(Element lacoolElement, Condition condition) {
        Element errorElement = lacoolElement.addElement("error");
        errorElement.addAttribute("code", condition.code + "");
        errorElement.addAttribute("msg", condition.msg);

        return lacoolElement;
    }

    public enum Condition {
        username_exist("username-exist", 100),
        auth_code_error("auth-code-error", 102),
        username_not_exist("username-not-exist", 103),
        auth_code_overload("auth-code-overload", 104),
        sms_server_error("sms-server-error", 105),
        room_member_overload("room-member-overload", 201),
        receipt_exist("receipt-exist", 400),
        receipt_iap_valid_fail("receipt-iap-valid-fail", 401),
        iapid_not_exist("iapid-not-exist", 402);
        private String msg;
        private int code;


        private Condition(String msg, int code) {
            this.msg = msg;
            this.code = code;
        }
    }
}
