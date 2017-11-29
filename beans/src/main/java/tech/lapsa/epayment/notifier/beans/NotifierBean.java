package tech.lapsa.epayment.notifier.beans;

import static tech.lapsa.epayment.notifier.beans.NotifierDestinations.*;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.Destination;

import tech.lapsa.epayment.notifier.Notification;
import tech.lapsa.epayment.notifier.Notifier;
import tech.lapsa.javax.jms.JmsClientFactory;

@Stateless
public class NotifierBean implements Notifier {

    @Inject
    private JmsClientFactory jmsFactory;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void send(final Notification notification) {
	final Destination destination = resolveDestination(notification);
	jmsFactory.createSender(destination).send(notification.getEntity(), notification.getProperties());
    }

    @Resource(name = JNDI_JMS_DEST_PAYMENTLINK_REQUESTER_EMAIL)
    private Destination paymentLinkUserEmail;

    @Resource(name = JNDI_JMS_DEST_PAYMENTSUCCESS_REQUESTER_EMAIL)
    private Destination paymentSucessUserEmail;

    private Destination resolveDestination(final Notification notification) {
	switch (notification.getEvent()) {
	case PAYMENT_SUCCESS:
	    switch (notification.getChannel()) {
	    case EMAIL:
		switch (notification.getRecipientType()) {
		case REQUESTER:
		    return paymentSucessUserEmail;
		default:
		}
	    default:
	    }
	case PAYMENT_LINK:
	    switch (notification.getChannel()) {
	    case EMAIL:
		switch (notification.getRecipientType()) {
		case REQUESTER:
		    return paymentLinkUserEmail;
		default:
		}
	    default:
	    }
	}
	throw new IllegalStateException(String.format(
		"Can't resolve Destination for channel '%2$s' recipient '%3$s' stage '%1$s'",
		notification.getEvent(), // 1
		notification.getChannel(), // 2
		notification.getRecipientType() // 3
	));
    }
}
