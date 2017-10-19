package tech.lapsa.epayment.notifier.beans;

import static tech.lapsa.epayment.notifier.beans.Constants.*;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.lapsa.kkb.core.KKBOrder;

import tech.lapsa.epayment.notifier.NotificationChannel;
import tech.lapsa.epayment.notifier.NotificationRecipientType;
import tech.lapsa.epayment.notifier.NotificationRequestStage;
import tech.lapsa.epayment.notifier.Notifier;
import tech.lapsa.java.commons.function.MyObjects;

@Stateless
public class NotifierBean implements Notifier {

    private final Logger logger = Logger.getLogger(Notifier.class.getPackage().getName());

    @Resource(name = JNDI_JMS_CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;

    @Resource(name = JNDI_JMS_DEST_PAYMENTLINK_REQUESTER_EMAIL)
    private Destination paymentLinkUserEmail;

    @Resource(name = JNDI_JMS_DEST_PAYMENTSUCCESS_REQUESTER_EMAIL)
    private Destination paymentSucessUserEmail;

    @Override
    public void assignOrderNotification(NotificationChannel channel, NotificationRecipientType recipientType,
	    NotificationRequestStage stage, KKBOrder order) {

	logger.info(String.format(
		"KKBOrder %1$s notification received on channel : %2$s, recipientType : %3$s requestStage : %4$s", //
		order, // 1
		channel, // 2
		recipientType, // 3
		stage // 4
	));

	newNotificationBuilder() //
		.withChannel(channel) //
		.withEvent(stage) //
		.withRecipient(recipientType) //
		.forEpayment(order) //
		.build() //
		.send();
    }

    @Override
    public NotificationBuilder newNotificationBuilder() {
	return new NotificationBuilderImpl();
    }

    private final class NotificationBuilderImpl implements NotificationBuilder {

	private NotificationChannel channel;
	private NotificationRecipientType recipientType;
	private NotificationRequestStage event;
	private KKBOrder epayment;

	private NotificationBuilderImpl() {
	}

	@Override
	public NotificationBuilder withChannel(NotificationChannel channel) {
	    this.channel = MyObjects.requireNonNull(channel, "channel");
	    return this;
	}

	@Override
	public NotificationBuilder withRecipient(NotificationRecipientType recipientType) {
	    this.recipientType = MyObjects.requireNonNull(recipientType, "recipientType");
	    return this;
	}

	@Override
	public NotificationBuilder withEvent(NotificationRequestStage event) {
	    this.event = MyObjects.requireNonNull(event, "event");
	    return this;
	}

	@Override
	public NotificationBuilder forEpayment(KKBOrder epayment) {
	    this.epayment = MyObjects.requireNonNull(epayment, "epayment");
	    return this;
	}

	@Override
	public Notification build() {
	    MyObjects.requireNonNull(epayment, "request");
	    Destination destination = resolveDestination();

	    return new NotificationImpl(destination);
	}

	private Destination resolveDestination() {
	    MyObjects.requireNonNull(epayment, "epayment");
	    MyObjects.requireNonNull(event, "event");
	    MyObjects.requireNonNull(recipientType, "recipientType");
	    MyObjects.requireNonNull(channel, "channel");

	    switch (event) {
	    case PAYMENT_SUCCESS:
		switch (channel) {
		case EMAIL:
		    switch (recipientType) {
		    case REQUESTER:
			return paymentSucessUserEmail;
		    default:
		    }
		default:
		}
	    case PAYMENT_LINK:
		switch (channel) {
		case EMAIL:
		    switch (recipientType) {
		    case REQUESTER:
			return paymentLinkUserEmail;
		    default:
		    }
		default:
		}
	    }

	    throw new IllegalStateException(String.format(
		    "Can't resolve Destination for channel '%2$s' recipient '%3$s' stage '%1$s'",
		    event, // 1
		    channel, // 2
		    recipientType // 3
	    ));
	}

	private final class NotificationImpl implements Notification {

	    private final Destination destination;
	    private final KKBOrder request;
	    private boolean sent = false;

	    private NotificationImpl(Destination destination) {
		this.destination = MyObjects.requireNonNull(destination, "destination");
		this.request = MyObjects.requireNonNull(NotificationBuilderImpl.this.epayment, "request");
	    }

	    @Override
	    public void send() {
		if (sent)
		    throw new IllegalStateException("Already sent");
		try (Connection connection = connectionFactory.createConnection()) {
		    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		    MessageProducer producer = session.createProducer(destination);
		    Message msg = session.createObjectMessage(request);
		    producer.send(msg);
		    sent = true;
		} catch (JMSException e) {
		    throw new RuntimeException("Failed to assign a notification task", e);
		}
	    }
	}
    }
}
