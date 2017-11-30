package tech.lapsa.epayment.notifier.beans;

public final class NotifierDestinations {

    private NotifierDestinations() {
    }

    public static final String JNDI_JMS_DEST_PAYMENTLINK_REQUESTER_EMAIL = "epayment/jms/events/notifier/paymentLinkUserEmail";
    public static final String JNDI_JMS_DEST_PAYMENTSUCCESS_REQUESTER_EMAIL = "epayment/jms/events/notifier/paymentSuccessUserEmail";
}
