package tech.lapsa.epayment.notifier.beans;

import static tech.lapsa.epayment.notifier.beans.Constants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import tech.lapsa.epayment.domain.Invoice;
import tech.lapsa.epayment.notifier.NotificationChannel;
import tech.lapsa.epayment.notifier.NotificationRecipientType;
import tech.lapsa.epayment.notifier.NotificationRequestStage;
import tech.lapsa.epayment.notifier.Notifier;
import tech.lapsa.java.commons.function.MyExceptions;
import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.java.commons.function.MyStrings;

@Stateless
public class NotifierBean implements Notifier {

    @Resource(name = JNDI_JMS_CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;

    @Resource(name = JNDI_JMS_DEST_PAYMENTLINK_REQUESTER_EMAIL)
    private Destination paymentLinkUserEmail;

    @Resource(name = JNDI_JMS_DEST_PAYMENTSUCCESS_REQUESTER_EMAIL)
    private Destination paymentSucessUserEmail;

    @Override
    public NotificationBuilder newNotificationBuilder() {
	return new NotificationBuilderImpl();
    }

    private final class NotificationBuilderImpl implements NotificationBuilder {

	private NotificationChannel channel;
	private NotificationRecipientType recipientType;
	private NotificationRequestStage event;
	private Invoice invoice;
	private final Map<String, String> properties = new HashMap<>();

	private NotificationBuilderImpl() {
	}

	@Override
	public NotificationBuilder withChannel(final NotificationChannel channel) {
	    this.channel = MyObjects.requireNonNull(channel, "channel");
	    return this;
	}

	@Override
	public NotificationBuilder withRecipient(final NotificationRecipientType recipientType) {
	    this.recipientType = MyObjects.requireNonNull(recipientType, "recipientType");
	    return this;
	}

	@Override
	public NotificationBuilder withEvent(final NotificationRequestStage event) {
	    this.event = MyObjects.requireNonNull(event, "event");
	    return this;
	}

	@Override
	public NotificationBuilder forEntity(final Invoice invoice) {
	    this.invoice = MyObjects.requireNonNull(invoice, "invoice");
	    return this;
	}

	@Override
	public NotificationBuilder withProperty(final String name, final String value) {
	    MyStrings.requireNonEmpty(name, "name");
	    MyStrings.requireNonEmpty(value, "value");
	    if (properties.containsKey(name))
		throw MyExceptions.illegalArgumentFormat("Already has property '%1$s'", name);
	    properties.put(name, value);
	    return this;
	}

	@Override
	public Notification build() {
	    MyObjects.requireNonNull(invoice, "request");
	    final Destination destination = resolveDestination();

	    return new NotificationImpl(destination);
	}

	private Destination resolveDestination() {
	    MyObjects.requireNonNull(invoice, "invoice");
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
	    private final Invoice invoice;
	    private boolean sent = false;
	    private Consumer<Notification> onSuccess;

	    private NotificationImpl(final Destination destination) {
		this.destination = MyObjects.requireNonNull(destination, "destination");
		invoice = MyObjects.requireNonNull(NotificationBuilderImpl.this.invoice, "invoice");
	    }

	    @Override
	    public Notification onSuccess(Consumer<Notification> onSuccess) {
		this.onSuccess = MyObjects.requireNonNull(onSuccess, "onSuccess");
		return this;
	    }

	    @Override
	    public void send() {
		if (sent)
		    throw new IllegalStateException("Already sent");
		try (Connection connection = connectionFactory.createConnection()) {
		    final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		    final MessageProducer producer = session.createProducer(destination);
		    final Message msg = session.createObjectMessage(invoice);
		    properties.entrySet() //
			    .stream() //
			    .forEach(x -> {
				try {
				    msg.setStringProperty(x.getKey(), x.getValue());
				} catch (final JMSException e) {
				    throw new RuntimeException("Failed to assign a property", e);
				}
			    });

		    producer.send(msg);
		    sent = true;
		    if (MyObjects.nonNull(onSuccess))
			onSuccess.accept(this);
		} catch (final JMSException e) {
		    throw new RuntimeException("Failed to assign a notification task", e);
		}
	    }
	}
    }
}
