package tech.lapsa.epayment.notifier.beans;

public final class Constants {

    private Constants() {
    }

    public static final String JNDI_MAIL_USER = "epayment/mail/messaging/UserNotification";

    public static final String JNDI_RESOURCE_CONFIGURATION = "epayment/resource/messaging/Configuration";

    public static final String JNDI_JMS_DEST_PAYMENTLINK_REQUESTER_EMAIL = "epayment/jms/messaging/paymentLinkUserEmail";
    public static final String JNDI_JMS_DEST_PAYMENTSUCCESS_REQUESTER_EMAIL = "epayment/jms/messaging/paymentSuccessUserEmail";

    public static final String PROPERTY_INSTANCE_VERB = "mesenger.instance.verb";
}
