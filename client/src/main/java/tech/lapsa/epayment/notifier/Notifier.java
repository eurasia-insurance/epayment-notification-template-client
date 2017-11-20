package tech.lapsa.epayment.notifier;

import java.util.function.Consumer;

import tech.lapsa.epayment.domain.Invoice;

public interface Notifier {

    @Deprecated
    default void assignOrderNotification(NotificationChannel channel, NotificationRecipientType recipientType,
	    NotificationRequestStage stage,
	    Invoice invoice) {
	newNotificationBuilder() //
		.withChannel(channel) //
		.withEvent(stage) //
		.withRecipient(recipientType) //
		.forEntity(invoice) //
		.build() //
		.send();
    }

    NotificationBuilder newNotificationBuilder();

    interface NotificationBuilder {

	NotificationBuilder withChannel(NotificationChannel channel);

	NotificationBuilder withRecipient(NotificationRecipientType recipientType);

	NotificationBuilder withEvent(NotificationRequestStage stage);

	NotificationBuilder forEntity(Invoice invoice);

	NotificationBuilder withProperty(String name, String value);

	Notification build();

	interface Notification {

	    Notification onSuccess(Consumer<Notification> cons);

	    void send();
	}
    }
}
